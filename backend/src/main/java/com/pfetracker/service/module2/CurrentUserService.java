package com.pfetracker.service.module2;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * MODIF : lisait auparavant les headers X-User-Id / X-User-Role envoyés tels
 * quels par le client (aucune vérification — n'importe qui pouvait se faire
 * passer pour n'importe quel utilisateur). Le Module 1/JWT est maintenant en
 * place : on lit l'utilisateur réellement authentifié depuis le contexte
 * Spring Security, peuplé par le JwtAuthenticationFilter du Module 1 pour
 * toutes les routes /v2/** (voir config/module2/SecurityConfigM2).
 */
@Service
public class CurrentUserService {

    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Long id)) {
            throw new IllegalStateException("Utilisateur non authentifié");
        }
        return id;
    }

    /** Renvoie le rôle simplifié attendu par le code métier existant du module2. */
    public String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new IllegalStateException("Utilisateur non authentifié");
        }

        String roleReel = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        return switch (roleReel) {
            case "ROLE_ETUDIANT" -> "STUDENT";
            case "ROLE_ENCADRANT" -> "ENCADRANT";
            case "ROLE_CHEF_DEPARTEMENT" -> "DEPT_MANAGER";
            case "ROLE_DIRECTEUR" -> "DIRECTOR";
            case "ROLE_ADMIN" -> "ADMIN";
            case "ROLE_SERVICE_STAGE" -> "SERVICE_STAGE";
            default -> roleReel;
        };
    }

    public boolean isStudent() {
        return "STUDENT".equals(getCurrentUserRole());
    }

    public boolean isSupervisor() {
        return "SUPERVISOR".equals(getCurrentUserRole())
                || "ENCADRANT".equals(getCurrentUserRole());
    }
}