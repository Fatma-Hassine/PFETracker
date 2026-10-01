package com.pfetracker.security.module1;

import com.pfetracker.service.module1.RateLimiterService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Rate limiting sur les endpoints sensibles (§7.1 du cahier des charges) :
 * connexion = 10 requêtes/minute, inscription = 5 requêtes/minute, par IP.
 */
@Component("rateLimitFilterM1")
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimiterService rateLimiterService;

    private static final int MAX_CONNEXION_PAR_MIN = 10;
    private static final int MAX_INSCRIPTION_PAR_MIN = 5;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();
        Integer limite = switch (path) {
            case "/auth/connexion" -> MAX_CONNEXION_PAR_MIN;
            case "/auth/inscription" -> MAX_INSCRIPTION_PAR_MIN;
            default -> null;
        };

        if (limite != null) {
            String cle = path + ":" + request.getRemoteAddr();
            if (!rateLimiterService.isAllowed(cle, limite)) {
                response.setStatus(429);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write(
                        "{\"message\":\"Trop de tentatives. Réessayez dans une minute.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
