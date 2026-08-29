package com.wedding.rsvpplatform.security;

import com.wedding.rsvpplatform.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Issues two kinds of session token.
 *
 * <p><b>Guest tokens</b> carry the guest id and the list of event keys that guest may see.
 * The front end holds one in memory only — never localStorage, never the URL — so it dies on
 * refresh, which is what "ask every time" means in practice. Every request for a private
 * event re-reads the list from the signed token rather than trusting the caller.
 *
 * <p><b>Admin tokens</b> are the existing username/password session.
 */
@Service
public class JwtService {

    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_EVENTS = "events";
    private static final String ROLE_GUEST = "GUEST";

    private final SecretKey key;
    private final int adminMinutes;
    private final int guestMinutes;

    public JwtService(AppProperties appProperties) {
        byte[] bytes = appProperties.jwt().secret().getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(bytes.length >= 32 ? bytes : pad(bytes));
        this.adminMinutes = appProperties.jwt().adminExpirationMinutes();
        this.guestMinutes = appProperties.jwt().guestExpirationMinutes();
    }

    private byte[] pad(byte[] bytes) {
        byte[] padded = new byte[32];
        System.arraycopy(bytes, 0, padded, 0, Math.min(bytes.length, 32));
        return padded;
    }

    public String generateAdminToken(String username, String role) {
        return build(username, role, null, adminMinutes);
    }

    public String generateGuestToken(UUID guestId, List<String> eventKeys) {
        return build(guestId.toString(), ROLE_GUEST, eventKeys, guestMinutes);
    }

    private String build(String subject, String role, List<String> eventKeys, int minutes) {
        Instant now = Instant.now();
        var builder = Jwts.builder()
                .subject(subject)
                .claim(CLAIM_ROLE, role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(minutes, ChronoUnit.MINUTES)))
                .signWith(key);
        if (eventKeys != null) {
            builder.claim(CLAIM_EVENTS, eventKeys);
        }
        return builder.compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
