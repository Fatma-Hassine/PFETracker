package com.pfetracker.security.module3;

import com.pfetracker.entity.module1.Utilisateur;
import com.pfetracker.repository.module1.UtilisateurRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * MODIF : ce filtre validait auparavant les tokens avec le JwtTokenProvider
 * propre au module3 (clé/algorithme différents de ceux du Module 1), ce qui
 * rendait impossible l'accès aux routes /v3/** avec le token JWT réel émis
 * par /auth/connexion (Module 1) — la seule connexion utilisée par le
 * frontend. On valide désormais avec le JwtService du Module 1 et on
 * recharge l'utilisateur réel (table utilisateurs) pour construire le
 * UserPrincipal, afin qu'un seul et même token fonctionne sur toute
 * l'application.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final com.pfetracker.security.module1.JwtService jwtServiceM1;
    private final UtilisateurRepository utilisateurRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String jwt = getJwtFromRequest(request);
            if (StringUtils.hasText(jwt)) {
                String email = jwtServiceM1.extraireEmail(jwt);
                Utilisateur utilisateur = email != null
                        ? utilisateurRepo.findByEmail(email).orElse(null)
                        : null;

                if (utilisateur != null && jwtServiceM1.estValide(jwt, utilisateur)) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    new UserPrincipal(
                                            utilisateur.getId(),
                                            utilisateur.getEmail(),
                                            utilisateur.getRole().name()
                                    ),
                                    null,
                                    utilisateur.getAuthorities()
                            );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Authenticated user: {}, role: {}", email, utilisateur.getRole());
                }
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}