package com.pfetracker.config.module1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.web.cors.*;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration("corsConfigM1")
public class CorsConfig {

	    @Value("${app.cors.allowed-origins}")
	    private List<String> allowedOrigins;

	    @Bean
	    public CorsFilter corsFilter() {
	        CorsConfiguration config = new CorsConfiguration();

	        config.setAllowedOrigins(allowedOrigins);
	        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
	        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
	        config.setExposedHeaders(List.of("Authorization"));
	        config.setAllowCredentials(true);
	        config.setMaxAge(3600L);

	        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	        source.registerCorsConfiguration("/api/**", config);

	        return new CorsFilter(source);
	    }
}
