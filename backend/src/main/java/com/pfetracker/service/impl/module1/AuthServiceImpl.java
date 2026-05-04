package com.pfetracker.service.impl.module1;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pfetracker.dto.module1.AuthResponse;
import com.pfetracker.dto.module1.ChangerMotDePasseRequest;
import com.pfetracker.dto.module1.InscriptionRequest;
import com.pfetracker.dto.module1.LoginRequest;
import com.pfetracker.entity.module1.Encadrant;
import com.pfetracker.entity.module1.Etudiant;
import com.pfetracker.entity.module1.PasswordResetToken;
import com.pfetracker.entity.module1.RefreshToken;
import com.pfetracker.entity.module1.Utilisateur;
import com.pfetracker.repository.module1.*;
import com.pfetracker.security.module1.*;
import com.pfetracker.service.module1.*;

import java.time.Duration;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.pfetracker.exception.module1.BusinessException;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UtilisateurRepository utilisateurRepo;
    private final RefreshTokenRepository refreshTokenRepo;
    private final PasswordResetTokenRepository resetTokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final NotificationService notificationService;
    private final LogAuditService logAuditService;

    private static final int DUREE_VERROUILLAGE_MINUTES = 30;
    private static final int MAX_TENTATIVES = 5;

    // MODIF : adresse IP par défaut pour les actions sans IP (système)
    private static final String IP_SYSTEME = "system";

    @Override
    public void inscrire(InscriptionRequest req) {
        if (!req.getEmail().endsWith("@enicar.ucar.tn")) {
            throw new BusinessException("Email institutionnel requis");
        }
        if (utilisateurRepo.existsByEmail(req.getEmail())) {
            throw new BusinessException("Email déjà utilisé");
        }

        String motDePasseTemp = genererMotDePasse();
        Utilisateur user = creerUtilisateurSelon(req, motDePasseTemp);
        utilisateurRepo.save(user);

        // MODIF : try/catch pour ne pas bloquer l'inscription si l'email échoue
        try {
            notificationService.envoyerEmailBienvenue(user.getEmail(), motDePasseTemp);
        } catch (Exception e) {
            System.err.println("⚠️ Email non envoyé pour "
                    + user.getEmail() + " : " + e.getMessage());
        }
    }

    @Override
    public AuthResponse connecter(LoginRequest req) {
        Utilisateur user = utilisateurRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Identifiants incorrects"));

        // Déverrouillage automatique après 30 minutes
        if (user.isAccountLocked() && user.getLockTime() != null) {
            if (LocalDateTime.now().isAfter(
                    user.getLockTime().plusMinutes(DUREE_VERROUILLAGE_MINUTES))) {
                user.setAccountLocked(false);
                user.setFailedLoginAttempts(0);
            }
        }

        if (user.isAccountLocked()) {
            logAuditService.log(user, "CONNEXION", req.getAdresseIp(),
                    "ECHEC", "Compte verrouillé");
            throw new LockedException("Compte verrouillé. Réessayez dans 30 minutes.");
        }

        // Si compte non activé ET pas première connexion → bloquer
        if (!user.isEnabled() && !user.isMustChangePassword()) {
            logAuditService.log(user, "CONNEXION", req.getAdresseIp(),
                    "ECHEC", "Compte en attente de validation");
            throw new BusinessException(
                    "Compte en attente de validation par le responsable");
        }

        try {
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getEmail(), req.getMotDePasse())
            );
        } catch (BadCredentialsException e) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            if (user.getFailedLoginAttempts() >= MAX_TENTATIVES) {
                user.setAccountLocked(true);
                user.setLockTime(LocalDateTime.now());
            }
            utilisateurRepo.save(user);
            logAuditService.log(user, "CONNEXION", req.getAdresseIp(),
                    "ECHEC", "Mauvais mot de passe");
            throw e;
        }

        user.setFailedLoginAttempts(0);
        utilisateurRepo.save(user);

        String accessToken = jwtService.genererAccessToken(user);
        RefreshToken rt    = creerRefreshToken(user);

        logAuditService.log(user, "CONNEXION", req.getAdresseIp(), "SUCCES", null);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(rt.getToken())
                .mustChangePassword(user.isMustChangePassword())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public AuthResponse rafraichirToken(String token) {
        RefreshToken rt = refreshTokenRepo.findByToken(token)
                .orElseThrow(() -> new BusinessException("Refresh token invalide"));

        if (rt.isRevoked() || rt.isExpired()) {
            throw new BusinessException("Refresh token expiré ou révoqué");
        }

        String newAccessToken = jwtService.genererAccessToken(rt.getUtilisateur());
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(token)
                .build();
    }

    @Override
    public void deconnecter(String token) {
        refreshTokenRepo.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepo.save(rt);
            // MODIF : IP_SYSTEME car la déconnexion n'a pas toujours d'IP
            logAuditService.log(rt.getUtilisateur(), "DECONNEXION",
                    IP_SYSTEME, "SUCCES", null);
        });
    }

    @Override
    public void demanderReinitialisationMotDePasse(String email) {
        utilisateurRepo.findByEmail(email).ifPresent(user -> {
            PasswordResetToken prt = new PasswordResetToken();
            prt.setToken(UUID.randomUUID().toString());
            prt.setUtilisateur(user);
            prt.setExpiration(LocalDateTime.now().plusHours(1));
            resetTokenRepo.save(prt);
            try {
                notificationService.envoyerLienReinitialisation(email, prt.getToken());
            } catch (Exception e) {
                System.err.println("⚠️ Email reset non envoyé : " + e.getMessage());
            }
        });
    }

    @Override
    public void reinitialiserMotDePasse(String token, String nouveauMotDePasse) {
        PasswordResetToken prt = resetTokenRepo.findByToken(token)
                .orElseThrow(() -> new BusinessException("Token invalide"));

        if (prt.isUsed() || prt.isExpired()) {
            throw new BusinessException("Token expiré ou déjà utilisé");
        }

        validerCritereMotDePasse(nouveauMotDePasse);

        Utilisateur user = prt.getUtilisateur();
        user.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse));
        prt.setUsed(true);

        utilisateurRepo.save(user);
        resetTokenRepo.save(prt);

        // MODIF : IP_SYSTEME car la réinitialisation se fait via lien email
        logAuditService.log(user, "REINITIALISATION_MDP", IP_SYSTEME, "SUCCES", null);
    }

    // MODIF : signature (String email) au lieu de (Long userId)
    // car Spring Security injecte UserDetails.getUsername() = email
    @Override
    public void changerMotDePasse(String email, ChangerMotDePasseRequest req) {
        Utilisateur user = utilisateurRepo.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));

        if (!passwordEncoder.matches(req.getAncienMotDePasse(), user.getMotDePasse())) {
            throw new BusinessException("Ancien mot de passe incorrect");
        }

        validerCritereMotDePasse(req.getNouveauMotDePasse());

        user.setMotDePasse(passwordEncoder.encode(req.getNouveauMotDePasse()));
        // MODIF : désactiver le flag mustChangePassword après 1ère connexion
        user.setMustChangePassword(false);
        // MODIF : activer le compte après changement du mot de passe temporaire
        user.setEnabled(true);
        utilisateurRepo.save(user);

        // MODIF : IP_SYSTEME au lieu de null
        // car la colonne adresse_ip est NOT NULL en base de données
        // et cette action ne dispose pas d'une adresse IP client
        try {
            logAuditService.log(user, "CHANGEMENT_MDP", IP_SYSTEME, "SUCCES", null);
        } catch (Exception e) {
            // Ne pas bloquer le changement de mot de passe si le log échoue
            System.err.println("⚠️ Log audit échoué : " + e.getMessage());
        }
    }

    // ── Méthodes privées ─────────────────────────────────────────────────────

    private String genererMotDePasse() {
        String chars =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%&*!";
        StringBuilder sb = new StringBuilder();
        java.security.SecureRandom rnd = new java.security.SecureRandom();
        for (int i = 0; i < 12; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }

    private void validerCritereMotDePasse(String mdp) {
        if (mdp.length() < 8)
            throw new BusinessException("Minimum 8 caractères requis");
        if (!mdp.matches(".*[A-Z].*"))
            throw new BusinessException("Au moins 1 lettre majuscule requise");
        if (!mdp.matches(".*[0-9].*"))
            throw new BusinessException("Au moins 1 chiffre requis");
        if (!mdp.matches(".*[^a-zA-Z0-9].*"))
            throw new BusinessException("Au moins 1 caractère spécial requis");
    }

    private RefreshToken creerRefreshToken(Utilisateur user) {
        RefreshToken rt = new RefreshToken();
        rt.setToken(UUID.randomUUID().toString());
        rt.setUtilisateur(user);
        rt.setExpirationDate(Instant.now().plus(Duration.ofDays(7)));
        return refreshTokenRepo.save(rt);
    }

    private Utilisateur creerUtilisateurSelon(InscriptionRequest req, String mdpTemp) {
        Utilisateur user = switch (req.getRole()) {
            case ROLE_ETUDIANT  -> new Etudiant();
            case ROLE_ENCADRANT -> new Encadrant();
            default -> throw new BusinessException(
                    "Rôle non autorisé à l'auto-inscription");
        };
        user.setEmail(req.getEmail());
        user.setNomComplet(req.getNomComplet());
        user.setMotDePasse(passwordEncoder.encode(mdpTemp));
        user.setRole(req.getRole());
        user.setMustChangePassword(true);
        user.setEnabled(false);
        return user;
    }
}