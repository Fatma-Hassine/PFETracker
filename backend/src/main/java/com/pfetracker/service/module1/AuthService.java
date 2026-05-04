package com.pfetracker.service.module1;

import com.pfetracker.dto.module1.*;

// MODIF : suppression de @Service sur l'interface (les interfaces ne sont pas des beans)
public interface AuthService {

    void inscrire(InscriptionRequest request);

    AuthResponse connecter(LoginRequest request);

    AuthResponse rafraichirToken(String refreshToken);

    void deconnecter(String refreshToken);

    void demanderReinitialisationMotDePasse(String email);

    void reinitialiserMotDePasse(String token, String nouveauMotDePasse);

    // MODIF : signature changée de (Long userId) vers (String email)
    // car @AuthenticationPrincipal retourne UserDetails dont on extrait l'username (email)
    // et non un Long directement
    void changerMotDePasse(String email, ChangerMotDePasseRequest request);
}