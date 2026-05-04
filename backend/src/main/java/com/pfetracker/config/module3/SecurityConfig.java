package com.pfetracker.config.module3;

import com.pfetracker.security.module3.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

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

                .requestMatchers(
                    "/api/v3/messages/**",
                    "/v3/messages/**"
                ).hasAnyRole("STUDENT", "SUPERVISOR")

                .requestMatchers(
                    "/api/v3/comments/**",
                    "/v3/comments/**"
                ).hasAnyRole("STUDENT", "SUPERVISOR")

                .requestMatchers(
                    "/api/v3/notifications/**",
                    "/v3/notifications/**"
                ).authenticated()

                .requestMatchers(
                    "/api/v3/meetings/**",
                    "/v3/meetings/**"
                ).hasAnyRole("STUDENT", "SUPERVISOR")

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
}