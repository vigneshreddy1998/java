package com.wedding.rsvpplatform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    private final AppProperties appProperties;

    public CorsConfig(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    /**
     * Exposed as a CorsConfigurationSource (not a standalone CorsFilter) so SecurityConfig can
     * wire it in via http.cors(...) — that's what makes Spring Security let CORS preflight
     * (OPTIONS) requests through *before* running authorization checks. A plain CorsFilter bean
     * runs after the security filter chain, so a protected endpoint's preflight would get
     * rejected by hasRole(...) (preflights never carry the Authorization header) before the
     * filter ever got a chance to add the CORS headers — which is exactly what was happening.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(appProperties.frontendOrigin()));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
