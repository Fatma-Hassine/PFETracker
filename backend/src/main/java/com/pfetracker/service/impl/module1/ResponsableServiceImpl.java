package com.pfetracker.service.impl.module1;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pfetracker.repository.module1.*;
import com.pfetracker.service.module1.*;
import com.pfetracker.dto.module1.*;
import com.pfetracker.entity.module1.*;
import com.pfetracker.entity.module1.enums.TypeNotification;
import com.pfetracker.exception.module1.BusinessException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ResponsableServiceImpl implements ResponsableService {

    private final UtilisateurRepository utilisateurRepo;
    private final EtudiantRepository etudiantRepo;
    private final EncadrantRepository encadrantRepo;
    private final DepartementRepository departementRepo;
    private final ResponsableDepartementRepository responsableRepo;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final LogAuditService logAuditService;


    @Override
    public List<UtilisateurResponse> getComptesEnAttente(Long departementId) {
        return encadrantRepo.findByDepartementIdAndEnabledFalse(departementId)
                .stream()
                .map(UtilisateurResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void validerCompte(Long userId) {
        Utilisateur user = utilisateurRepo.findById(userId)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));

        user.setEnabled(true);
        utilisateurRepo.save(user);

        notificationService.creerNotification(
                userId,
                TypeNotification.COMPTE_VALIDE,
                "Votre compte a été validé. Vous pouvez maintenant vous connecter."
        );

        logAuditService.log(user, "VALIDATION_COMPTE", null, "SUCCES", null);
    }

    @Override
    public void refuserCompte(Long userId, String motif) {
        Utilisateur user = utilisateurRepo.findById(userId)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));

        user.setEnabled(false);
        utilisateurRepo.save(user);

        notificationService.creerNotification(
                userId,
                TypeNotification.COMPTE_REFUSE,
                "Votre demande de compte a été refusée. Motif : " + motif
        );

        logAuditService.log(user, "REFUS_COMPTE", null, "SUCCES", "Motif : " + motif);
    }

    @Override
    public void activerDesactiverCompte(Long userId, boolean activer) {
        Utilisateur user = utilisateurRepo.findById(userId)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));

        user.setEnabled(activer);
        utilisateurRepo.save(user);

        logAuditService.log(
                user,
                activer ? "ACTIVATION_COMPTE" : "DESACTIVATION_COMPTE",
                null,
                "SUCCES",
                null
        );
    }

    @Override
    public void deverrouillerCompte(Long userId) {
        Utilisateur user = utilisateurRepo.findById(userId)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));

        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        user.setLockTime(null);

        utilisateurRepo.save(user);

        logAuditService.log(user, "DEVERROUILLAGE_COMPTE", null, "SUCCES", null);
    }

    @Override
    public void reinitialiserMotDePasseForce(Long userId) {
        Utilisateur user = utilisateurRepo.findById(userId)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));

        String nouveauMdp = UUID.randomUUID().toString().substring(0, 10) + "A1!";

        user.setMotDePasse(passwordEncoder.encode(nouveauMdp));
        user.setMustChangePassword(true);

        utilisateurRepo.save(user);

        notificationService.envoyerEmailBienvenue(user.getEmail(), nouveauMdp);

        logAuditService.log(user, "REINITIALISATION_MDP_FORCE", null, "SUCCES", null);
    }

    @Override
    public void changerDepartement(Long userId, Long nouveauDepartementId) {
        Utilisateur user = utilisateurRepo.findById(userId)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable"));

        Departement dept = departementRepo.findById(nouveauDepartementId)
                .orElseThrow(() -> new BusinessException("Département introuvable"));

        if (user instanceof Etudiant e) {
            e.setDepartement(dept);
        } else if (user instanceof Encadrant enc) {
            enc.setDepartement(dept);
        } else {
            throw new BusinessException("Ce type d'utilisateur ne peut pas changer de département");
        }

        utilisateurRepo.save(user);

        logAuditService.log(
                user,
                "CHANGEMENT_DEPARTEMENT",
                null,
                "SUCCES",
                "Nouveau département : " + dept.getNom()
        );
    }


    @Override
    public List<EtudiantResponse> getEtudiantsSansEncadrant(Long departementId) {
        return etudiantRepo.findByDepartementIdAndEncadrantIsNull(departementId)
                .stream()
                .map(EtudiantResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void forcerAffectation(Long etudiantId, Long encadrantId) {
        Etudiant etudiant = etudiantRepo.findById(etudiantId)
                .orElseThrow(() -> new BusinessException("Étudiant introuvable"));

        Encadrant encadrant = encadrantRepo.findById(encadrantId)
                .orElseThrow(() -> new BusinessException("Encadrant introuvable"));

        ResponsableDepartement resp = responsableRepo.findByDepartementId(
                etudiant.getDepartement().getId()
        ).orElseThrow(() -> new BusinessException("Responsable introuvable"));

        long chargeActuelle = etudiantRepo.countByEncadrantId(encadrantId);

        if (chargeActuelle >= resp.getLimiteEtudiantsParEncadrant()) {
            throw new BusinessException(
                    "L'encadrant a atteint sa limite d'étudiants ("
                            + resp.getLimiteEtudiantsParEncadrant() + ")"
            );
        }

        etudiant.setEncadrant(encadrant);
        etudiantRepo.save(etudiant);

        logAuditService.log(
                etudiant,
                "AFFECTATION_FORCEE",
                null,
                "SUCCES",
                "Encadrant : " + encadrant.getNomComplet()
        );
    }

    @Override
    public void rompreAffectation(Long etudiantId) {
        Etudiant etudiant = etudiantRepo.findById(etudiantId)
                .orElseThrow(() -> new BusinessException("Étudiant introuvable"));

        Encadrant ancien = etudiant.getEncadrant();

        etudiant.setEncadrant(null);
        etudiantRepo.save(etudiant);

        logAuditService.log(
                etudiant,
                "RUPTURE_AFFECTATION",
                null,
                "SUCCES",
                ancien != null ? "Ancien encadrant : " + ancien.getNomComplet() : null
        );
    }

    @Override
    public List<EncadrantChargeResponse> getChargeEncadrants(Long departementId) {
        return encadrantRepo.findByDepartementId(departementId)
                .stream()
                .map(enc -> new EncadrantChargeResponse(
                        enc.getId(),
                        enc.getNomComplet(),
                        (int) etudiantRepo.countByEncadrantId(enc.getId())
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void configurerLimiteEtudiants(Long departementId, int limite) {
        ResponsableDepartement resp = responsableRepo.findByDepartementId(departementId)
                .orElseThrow(() -> new BusinessException("Responsable introuvable"));

        resp.setLimiteEtudiantsParEncadrant(limite);
        responsableRepo.save(resp);
    }


    @Override
    public DashboardDepartementResponse getDashboard(Long departementId) {
        long totalActifs = etudiantRepo.countByDepartementId(departementId);
        long sansEncadrant = etudiantRepo.countByDepartementIdAndEncadrantIsNull(departementId);

        return DashboardDepartementResponse.builder()
                .totalEtudiants(totalActifs)
                .etudiantsSansEncadrant(sansEncadrant)
                .build();
    }

    @Override
    public void regenererCodeEncadrant(Long encadrantId) {
        Encadrant enc = encadrantRepo.findById(encadrantId)
                .orElseThrow(() -> new BusinessException("Encadrant introuvable"));

        enc.regenererCode();
        encadrantRepo.save(enc);

        logAuditService.log(enc, "REGENERATION_CODE_ENCADRANT", null, "SUCCES", null);
    }

}