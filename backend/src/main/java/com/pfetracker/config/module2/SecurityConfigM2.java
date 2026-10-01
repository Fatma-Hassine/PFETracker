package com.pfetracker.config.module2;

import com.pfetracker.security.module1.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * MODIF : le module2 (PFE, jalons, tâches, livrables, sprints) n'avait
 * jusqu'ici AUCUNE chaîne de sécurité qui couvre ses routes /v2/** — aucun
 * des deux SecurityFilterChain existants (module1 : /auth,/admin,/responsable...
 * ; module3 : /api/v3,/v3,/ws...) ne matchait ces chemins. En Spring Security,
 * une requête qui ne correspond à AUCUN SecurityFilterChain enregistré
 * n'est filtrée par aucun d'eux : ces endpoints étaient donc accessibles
 * sans authentification, et l'identité de l'appelant reposait uniquement
 * sur des headers X-User-Id/X-User-Role envoyés par le client lui-même
 * (voir CurrentUserService, maintenant corrigé pour lire le contexte
 * Spring Security réel). On réutilise le JwtAuthenticationFilter du
 * Module 1 pour que le même token JWT (émis par /auth/connexion) protège
 * aussi ces routes.
 */
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Configuration("securityConfigM2")
public class SecurityConfigM2 {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean("securityFilterChainM2")
    @Order(3)
    public SecurityFilterChain securityFilterChainM2(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/v2/**")

            .cors(Customizer.withDefaults())
            .exceptionHandling(e -> e.authenticationEntryPoint(
                new org.springframework.security.web.authentication.HttpStatusEntryPoint(
                    org.springframework.http.HttpStatus.UNAUTHORIZED)))
            .csrf(AbstractHttpConfigurer::disable)

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated()
            )

            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
