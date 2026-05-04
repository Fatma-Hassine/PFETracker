package com.pfetracker.service.module2;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Service temporaire pour récupérer l'utilisateur courant.
 *
 * Pour le moment, on lit les headers :
 * X-User-Id
 * X-User-Role
 *
 * Plus tard, quand le Module 1 / JWT sera prêt, tu modifies seulement ce fichier
 * pour lire l'utilisateur depuis Spring Security / JWT.
 */
@Service
public class CurrentUserService {

    public Long getCurrentUserId() {
        HttpServletRequest request = getCurrentRequest();

        if (request == null) {
            return 1L;
        }

        String userIdHeader = request.getHeader("X-User-Id");

        if (userIdHeader == null || userIdHeader.isBlank()) {
            return 1L;
        }

        return Long.parseLong(userIdHeader);
    }

    public String getCurrentUserRole() {
        HttpServletRequest request = getCurrentRequest();

        if (request == null) {
            return "STUDENT";
        }

        String roleHeader = request.getHeader("X-User-Role");

        if (roleHeader == null || roleHeader.isBlank()) {
            return "STUDENT";
        }

        return roleHeader.toUpperCase();
    }

    public boolean isStudent() {
        return "STUDENT".equals(getCurrentUserRole());
    }

    public boolean isSupervisor() {
        return "SUPERVISOR".equals(getCurrentUserRole())
                || "ENCADRANT".equals(getCurrentUserRole());
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return null;
        }

        return attributes.getRequest();
    }
}