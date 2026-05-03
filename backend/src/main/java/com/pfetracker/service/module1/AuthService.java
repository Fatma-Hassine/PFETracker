package com.pfetracker.service.module1;

import org.springframework.stereotype.Service;

import com.pfetracker.dto.module1.*;
@Service("authServiceM1")
public interface AuthService {
	void inscrire(InscriptionRequest request);
    AuthResponse connecter(LoginRequest request);
    AuthResponse rafraichirToken(String refreshToken);
    void deconnecter(String refreshToken);
    void demanderReinitialisationMotDePasse(String email);
    void reinitialiserMotDePasse(String token, String nouveauMotDePasse);
    void changerMotDePasse(Long userId, ChangerMotDePasseRequest request);
}
