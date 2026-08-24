package com.wedding.rsvpplatform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        String frontendOrigin,
        Jwt jwt,
        Admin admin,
        Llm llm
) {
    public record Jwt(String secret, int expirationMinutes) {}
    public record Admin(String bootstrapUsername, String bootstrapPassword) {}
    public record Llm(String provider, String apiKey, String model, String baseUrl) {}
}
