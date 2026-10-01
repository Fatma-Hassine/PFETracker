package com.pfetracker.config.module1;

import com.pfetracker.security.module1.CustomUserDetailsService;
import com.pfetracker.security.module1.JwtAuthenticationFilter;
import com.pfetracker.security.module1.RateLimitFilter;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
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
    private final RateLimitFilter rateLimitFilter;

    @Bean("securityFilterChainM1")
    @Order(1)
    public SecurityFilterChain securityFilterChainM1(HttpSecurity http) throws Exception {
        http
            .securityMatcher(
                "/auth/**",
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
            // 401 (et non 403) si token absent/expiré : le frontend peut alors rafraîchir
            .exceptionHandling(e -> e.authenticationEntryPoint(
                new org.springframework.security.web.authentication.HttpStatusEntryPoint(
                    org.springframework.http.HttpStatus.UNAUTHORIZED)))
            .csrf(AbstractHttpConfigurer::disable)

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth

                // MODIF : OPTIONS toujours autorisé pour CORS preflight
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // MODIF : toutes les routes /auth/** sont publiques
                // (inscription, connexion, refresh, reset mdp, changer mdp)
                // car changer-mot-de-passe est appelé avec un token JWT valide
                // mais le compte peut ne pas être encore "enabled"
                .requestMatchers("/auth/**").permitAll()

                // Swagger public
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**"
                ).permitAll()

                // Routes protégées par rôle
                // MODIF : hasAuthority avec le nom exact de l'enum Role (getAuthorities()
                // renvoie déjà role.name() préfixé "ROLE_") — hasRole() préfixe "ROLE_"
                // automatiquement et les anciens noms (DEPT_MANAGER, etc.) ne correspondaient
                // à aucune valeur réelle de l'enum, ce qui bloquait ces routes pour tout le monde.
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/responsable/**").hasAuthority("ROLE_CHEF_DEPARTEMENT")
                .requestMatchers("/directeur/**").hasAuthority("ROLE_DIRECTEUR")
                .requestMatchers("/service-stages/**").hasAuthority("ROLE_SERVICE_STAGE")
                .requestMatchers("/logs/**").hasAuthority("ROLE_CHEF_DEPARTEMENT")

                .requestMatchers("/notifications/**", "/utilisateurs/**").authenticated()

                .anyRequest().authenticated()
            )

            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(rateLimitFilter, JwtAuthenticationFilter.class)
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

    // MODIF : bug transversal découvert en testant manuellement — Spring Boot
    // enregistre automatiquement TOUT bean @Component qui implémente Filter
    // comme filtre servlet GLOBAL (sur toutes les requêtes de l'app), en plus
    // de son câblage explicite ici via addFilterBefore(). Comme le
    // JwtAuthenticationFilter du module3 fait la même chose de son côté, les
    // DEUX filtres tournaient sur CHAQUE requête, et celui qui s'exécutait en
    // dernier écrasait le principal (Long) posé par celui-ci avec son propre
    // UserPrincipal — cassant silencieusement @AuthenticationPrincipal Long
    // partout dans l'app (profil, isolation département, etc.), alors même
    // que les vérifications par rôle continuaient de fonctionner (mêmes
    // autorités des deux côtés). On désactive l'enregistrement global pour
    // que ces filtres ne s'exécutent QUE via leur SecurityFilterChain dédiée.
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> disableGlobalRegistrationJwtM1(
            JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setEnabled(false);
        return reg;
    }

    @Bean
    public FilterRegistrationBean<RateLimitFilter> disableGlobalRegistrationRateLimit(
            RateLimitFilter filter) {
        FilterRegistrationBean<RateLimitFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setEnabled(false);
        return reg;
    }
}