package com.pfetracker.config.module1;
import com.pfetracker.security.*;
import com.pfetracker.security.module1.*;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Configuration("securityConfigM1")
public class SecurityConfig {
	 private final CustomUserDetailsService userDetailsService;
	    private final JwtAuthenticationFilter jwtAuthFilter;
	    private final CustomAccessDeniedHandler accessDeniedHandler;
	    private final CustomAuthenticationEntryPoint authEntryPoint;

	    @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	        http
	            // Désactivation CSRF (API REST stateless)
	            .csrf(AbstractHttpConfigurer::disable)

	            // Politique de session : STATELESS (JWT uniquement)
	            .sessionManagement(session ->
	                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

	            // Règles d'autorisation par endpoint
	            .authorizeHttpRequests(auth -> auth

	                // Routes publiques
	            		.requestMatchers(
	                        "/api/auth/inscription",
	                        "/api/auth/connexion",
	                        "/api/auth/refresh",
	                        "/api/auth/mot-de-passe-oublie",
	                        "/api/auth/reinitialiser-mot-de-passe"
	                ).permitAll()

	                // Routes Responsable département 
	                .requestMatchers("/api/responsable/**")
	                    .hasRole("DEPT_MANAGER")

	                // Routes Directeur des stages
	                .requestMatchers("/api/directeur/**")
	                    .hasRole("DIRECTOR")

	                // Routes Logs d'audit
	                .requestMatchers("/api/logs/**")
	                    .hasRole("DEPT_MANAGER")

	                //  Toutes les autres routes : authentification requise 
	                .anyRequest().authenticated()
	            )

	            // Gestionnaires d'erreurs personnalisés
	            .exceptionHandling(ex -> ex
	                .authenticationEntryPoint(authEntryPoint)
	                .accessDeniedHandler(accessDeniedHandler)
	            )

	            // Ajout du filtre JWT avant le filtre d'authentification standard
	            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

	            // Provider d'authentification personnalisé
	            .authenticationProvider(authenticationProvider());

	        return http.build();
	    }

	    @Bean
	    public DaoAuthenticationProvider authenticationProvider() {
	        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
	        provider.setUserDetailsService(userDetailsService);
	        provider.setPasswordEncoder(passwordEncoder());
	        return provider;
	    }

	    @Bean
	    public AuthenticationManager authenticationManager(
	            AuthenticationConfiguration config) throws Exception {
	        return config.getAuthenticationManager();
	    }

	    @Bean
	    public PasswordEncoder passwordEncoder() {
	        // BCrypt avec coût minimum 12 (requis par le cahier des charges)
	        return new BCryptPasswordEncoder(12);
	    }
}
