package com.pfetracker.security.module1;
import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	private final JwtService jwtService;
    private final UtilisateurRepository utilisateurRepo;

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

            // Si l'email est extrait et qu'aucune auth n'est déjà présente
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                Utilisateur utilisateur = utilisateurRepo.findByEmail(email).orElse(null);

                if (utilisateur != null && jwtService.estValide(jwt, utilisateur)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    utilisateur.getId(),          // principal = userId
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
            // On laisse passer — Spring Security rejettera la requête si besoin
        }

        filterChain.doFilter(request, response);
    }

    // Ne pas filtrer les routes publiques
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/auth/inscription")
                || path.startsWith("/api/auth/connexion")
                || path.startsWith("/api/auth/refresh")
                || path.startsWith("/api/auth/mot-de-passe-oublie")
                || path.startsWith("/api/auth/reinitialiser-mot-de-passe");
    }
}
