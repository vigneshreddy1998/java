package com.wedding.rsvpplatform.dto;

/**
 * Deliberately identical in shape whether the number was on the guest list or not — nobody
 * can tell from the response which path they took.
 */
public record VerifyResponse(
        String token,
        String guestName,
        boolean needsName
) {}
