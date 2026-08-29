package com.wedding.rsvpplatform.dto.admin;

import java.util.List;

public record ImportPreview(
        List<ParsedContact> contacts,
        int totalParsed,
        int invalidCount,
        int duplicateCount,
        /** False when cleanup was skipped (no API key, or the call failed) — names are raw. */
        boolean cleanupApplied,
        String cleanupNote
) {}
