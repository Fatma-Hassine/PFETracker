package com.pfetracker.controller.module1;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.pfetracker.dto.module1.*;
import com.pfetracker.repository.module1.DepartementRepository;
import com.pfetracker.security.module1.JwtService;
import com.pfetracker.service.module1.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RestController("authControllerM1")
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final DepartementRepository departementRepo;

    // MODIF : injection du JwtService pour extraire l'email du token
    private final JwtService jwtService;

    // Liste publique des départements pour le formulaire d'inscription —
    // le département est obligatoire dès l'inscription (voir InscriptionRequest).
    @GetMapping("/departements")
    public ResponseEntity<List<DepartementPublicResponse>> listerDepartements() {
        return ResponseEntity.ok(
                departementRepo.findAll().stream()
                        .map(d -> new DepartementPublicResponse(d.getId(), d.getNom()))
                        .collect(Collectors.toList())
        );
    }

    @PostMapping("/inscription")
    public ResponseEntity<Void> inscrire(
            @Valid @RequestBody InscriptionRequest request) {
        authService.inscrire(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/connexion")
    public ResponseEntity<AuthResponse> connecter(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        request.setAdresseIp(httpRequest.getRemoteAddr());
        return ResponseEntity.ok(authService.connecter(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> rafraichir(
            @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.rafraichirToken(request.getRefreshToken()));
    }

    @PostMapping("/deconnexion")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deconnecter(
            @RequestBody RefreshTokenRequest request) {
        authService.deconnecter(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/mot-de-passe-oublie")
    public ResponseEntity<Void> demanderReinit(
            @Valid @RequestBody EmailRequest request) {
        authService.demanderReinitialisationMotDePasse(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reinitialiser-mot-de-passe")
    public ResponseEntity<Void> reinitialiser(
            @Valid @RequestBody ReinitialisationRequest request) {
        authService.reinitialiserMotDePasse(
                request.getToken(), request.getNouveauMotDePasse());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/changer-mot-de-passe")
    // MODIF : pas de @PreAuthorize ni de @AuthenticationPrincipal
    // On extrait l'email directement depuis le token JWT dans le header
    // car le SecurityContext n'est pas toujours peuplé pour les comptes enabled=false
    public ResponseEntity<Void> changerMotDePasse(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody ChangerMotDePasseRequest request) {

        // Vérifier que le header Authorization est présent et valide
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        // MODIF : extraire le token et l'email via JwtService
        String token = authHeader.substring(7);
        String email;
        try {
            email = jwtService.extraireEmail(token);
        } catch (Exception e) {
            // Token invalide ou expiré
            return ResponseEntity.status(401).build();
        }

        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        authService.changerMotDePasse(email, request);
        return ResponseEntity.ok().build();
    }
}