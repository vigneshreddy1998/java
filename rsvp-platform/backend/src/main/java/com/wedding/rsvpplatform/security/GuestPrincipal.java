package com.wedding.rsvpplatform.security;

import java.util.List;
import java.util.UUID;

/**
 * The verified guest behind the current request, reconstructed from the signed session token.
 * {@code eventKeys} is the authoritative list of what this guest may see — controllers check
 * against this, never against anything the client sends.
 */
public record GuestPrincipal(UUID guestId, List<String> eventKeys) {

    public boolean canSee(String eventKey) {
        return eventKeys.contains(eventKey);
    }
}
