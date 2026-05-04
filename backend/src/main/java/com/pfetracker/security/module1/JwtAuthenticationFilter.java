package com.pfetracker.security.module1;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.pfetracker.entity.module1.Utilisateur;
import com.pfetracker.repository.module1.UtilisateurRepository;

@Component("jwtAuthenticationFilterM1")
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UtilisateurRepository utilisateurRepo;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/inscription",
            "/auth/connexion",
            "/auth/refresh",
            "/auth/mot-de-passe-oublie",
            "/auth/reinitialiser-mot-de-passe",
            "/swagger-ui",
            "/v3/api-docs"
    );

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Pas de header Authorization ou pas Bearer → on passe au filtre suivant
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        try {
            final String email = jwtService.extraireEmail(jwt);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                Utilisateur utilisateur = utilisateurRepo.findByEmail(email).orElse(null);

                if (utilisateur != null && jwtService.estValide(jwt, utilisateur)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    utilisateur.getId(),
                                    null,
                                    utilisateur.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        } catch (Exception e) {
            log.warn("Erreur lors du traitement du token JWT : {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        // Important pour CORS : laisser passer les requêtes preflight OPTIONS
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        // Ne pas filtrer les routes publiques
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }
}