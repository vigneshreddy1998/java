package com.wedding.rsvpplatform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        String frontendOrigin,
        String galleryUrl,
        Jwt jwt,
        Admin admin,
        Llm llm,
        Phone phone,
        RateLimit rateLimit
) {
    public record Jwt(String secret, int adminExpirationMinutes, int guestExpirationMinutes) {}

    public record Admin(String bootstrapUsername, String bootstrapPassword) {}

    public record Llm(String apiKey, String model) {}

    /** Region assumed when a number arrives without a country code. */
    public record Phone(String defaultRegion) {}

    /** Guards the phone lookup against someone working through numbers. */
    public record RateLimit(int maxAttempts, int windowMinutes) {}
}
