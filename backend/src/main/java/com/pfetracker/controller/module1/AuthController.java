package com.pfetracker.controller.module1;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.pfetracker.dto.module1.*;
import com.pfetracker.service.module1.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@RestController("authControllerM1")
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	 private final AuthService authService;

	    @PostMapping("/inscription")
	    public ResponseEntity<Void> inscrire(@Valid @RequestBody InscriptionRequest request) {
	        authService.inscrire(request);
	        return ResponseEntity.accepted().build();
	    }

	    @PostMapping("/connexion")
	    public ResponseEntity<AuthResponse> connecter(
	            @Valid @RequestBody LoginRequest request,
	            HttpServletRequest httpRequest) {
	        // Injection de l'IP pour la journalisation
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
	    public ResponseEntity<Void> deconnecter(@RequestBody RefreshTokenRequest request) {
	        authService.deconnecter(request.getRefreshToken());
	        return ResponseEntity.noContent().build();
	    }

	    @PostMapping("/mot-de-passe-oublie")
	    public ResponseEntity<Void> demanderReinit(@Valid @RequestBody EmailRequest request) {
	        authService.demanderReinitialisationMotDePasse(request.getEmail());
	        return ResponseEntity.ok().build();
	    }

	    @PostMapping("/reinitialiser-mot-de-passe")
	    public ResponseEntity<Void> reinitialiser(
	            @Valid @RequestBody ReinitialisationRequest request) {
	        authService.reinitialiserMotDePasse(request.getToken(), request.getNouveauMotDePasse());
	        return ResponseEntity.ok().build();
	    }

	    @PostMapping("/changer-mot-de-passe")
	    @PreAuthorize("isAuthenticated()")
	    public ResponseEntity<Void> changerMotDePasse(
	            @AuthenticationPrincipal Long userId,
	            @Valid @RequestBody ChangerMotDePasseRequest request) {
	        authService.changerMotDePasse(userId, request);
	        return ResponseEntity.ok().build();
	    }
}
