package com.pfetracker.config.module1;

import com.pfetracker.security.module1.CustomUserDetailsService;
import com.pfetracker.security.module1.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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

@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Configuration("securityConfigM1")
public class SecurityConfigM1 {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean("securityFilterChainM1")
    @Order(1)
    public SecurityFilterChain securityFilterChainM1(HttpSecurity http) throws Exception {
        http
            .securityMatcher(
                "/auth/**",
                "/api/auth/**",
                "/admin/**",
                "/responsable/**",
                "/directeur/**",
                "/service-stages/**",
                "/logs/**",
                "/notifications/**",
                "/utilisateurs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v3/api-docs/**"
            )

            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth

                // Important pour CORS
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Routes publiques Auth
                .requestMatchers(
                    "/auth/inscription",
                    "/auth/connexion",
                    "/auth/refresh",
                    "/auth/mot-de-passe-oublie",
                    "/auth/reinitialiser-mot-de-passe",
                    "/api/auth/inscription",   
                    "/api/auth/connexion",    
                    "/api/auth/refresh",    
                    "/api/auth/mot-de-passe-oublie",      
                    "/api/auth/reinitialiser-mot-de-passe"
                ).permitAll()

                // Swagger
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**"
                ).permitAll()

                // Routes protégées
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/responsable/**").hasRole("DEPT_MANAGER")
                .requestMatchers("/directeur/**").hasRole("DIRECTEUR")
                .requestMatchers("/service-stages/**").hasRole("SERVICE_STAGE")
                .requestMatchers("/logs/**").hasRole("DEPT_MANAGER")

                .requestMatchers("/notifications/**", "/utilisateurs/**").authenticated()

                .anyRequest().authenticated()
            )

            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

            .authenticationProvider(authenticationProviderM1());

        return http.build();
    }

    @Bean("authenticationProviderM1")
    public DaoAuthenticationProvider authenticationProviderM1() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoderM1());
        return provider;
    }

    @Bean("authenticationManagerM1")
    @Primary
    public AuthenticationManager authenticationManagerM1(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean("passwordEncoderM1")
    @Primary
    public PasswordEncoder passwordEncoderM1() {
        return new BCryptPasswordEncoder(12);
    }
}