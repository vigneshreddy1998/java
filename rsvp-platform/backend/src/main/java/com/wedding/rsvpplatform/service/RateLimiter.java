package com.wedding.rsvpplatform.service;

import com.wedding.rsvpplatform.config.AppProperties;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Fixed-window limiter on failed phone lookups, keyed by client address.
 *
 * <p>This is what makes a phone number an acceptable credential: someone can still guess a
 * number they already know, but they can't work through a range. Successful lookups clear the
 * counter, so a guest fat-fingering their own number a few times is never locked out for long.
 *
 * <p>In-memory on purpose — a single backend instance serves one wedding, and a restart
 * clearing the counters is not a meaningful weakness at this scale. Move to Redis only if the
 * backend is ever horizontally scaled.
 */
@Service
public class RateLimiter {

    private record Window(int attempts, Instant startedAt) {}

    private final Map<String, Window> windows = new ConcurrentHashMap<>();
    private final int maxAttempts;
    private final Duration window;

    public RateLimiter(AppProperties appProperties) {
        this.maxAttempts = appProperties.rateLimit().maxAttempts();
        this.window = Duration.ofMinutes(appProperties.rateLimit().windowMinutes());
    }

    public boolean isBlocked(String key) {
        Window current = windows.get(key);
        if (current == null) {
            return false;
        }
        if (isExpired(current)) {
            windows.remove(key);
            return false;
        }
        return current.attempts() >= maxAttempts;
    }

    public void recordFailure(String key) {
        windows.compute(key, (k, current) -> {
            if (current == null || isExpired(current)) {
                return new Window(1, Instant.now());
            }
            return new Window(current.attempts() + 1, current.startedAt());
        });
    }

    public void recordSuccess(String key) {
        windows.remove(key);
    }

    private boolean isExpired(Window w) {
        return Instant.now().isAfter(w.startedAt().plus(window));
    }
}
