package com.pfetracker.config.module3;

import com.pfetracker.security.module3.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration("securityConfigM3")
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean("securityFilterChainM3")
    @Order(2)
    public SecurityFilterChain filterChainM3(HttpSecurity http) throws Exception {
        http
            .securityMatcher(
                "/api/v3/**",
                "/v3/**",
                "/ws/**",
                "/topic/**",
                "/app/**"
            )

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

                .requestMatchers(
                    "/ws/**",
                    "/topic/**",
                    "/app/**"
                ).permitAll()

                .requestMatchers(
                    "/api/v3/public/**",
                    "/v3/public/**"
                ).permitAll()

                // MODIF : hasAnyAuthority avec les noms réels de l'enum Role du Module 1
                // (ROLE_ETUDIANT / ROLE_ENCADRANT) — "STUDENT"/"SUPERVISOR" ne
                // correspondaient à aucune autorité réelle et bloquaient tout le monde.
                .requestMatchers(
                    "/api/v3/messages/**",
                    "/v3/messages/**"
                ).hasAnyAuthority("ROLE_ETUDIANT", "ROLE_ENCADRANT")

                .requestMatchers(
                    "/api/v3/comments/**",
                    "/v3/comments/**"
                ).hasAnyAuthority("ROLE_ETUDIANT", "ROLE_ENCADRANT")

                .requestMatchers(
                    "/api/v3/notifications/**",
                    "/v3/notifications/**"
                ).authenticated()

                .requestMatchers(
                    "/api/v3/meetings/**",
                    "/v3/meetings/**"
                ).hasAnyAuthority("ROLE_ETUDIANT", "ROLE_ENCADRANT")

                .requestMatchers(
                    "/api/v3/dashboard/**",
                    "/v3/dashboard/**"
                ).authenticated()

                .anyRequest().authenticated()
            )

            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean("passwordEncoderM3")
    public PasswordEncoder passwordEncoderM3() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean("authenticationManagerM3")

    public AuthenticationManager authenticationManagerM3(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // MODIF : voir SecurityConfigM1 — empêche Spring Boot d'enregistrer ce
    // filtre comme filtre servlet global (il ne doit s'exécuter que sur la
    // chaîne /v3/** via addFilterBefore ci-dessus).
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> disableGlobalRegistrationJwtM3(
            JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setEnabled(false);
        return reg;
    }
}