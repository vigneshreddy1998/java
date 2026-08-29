package com.wedding.rsvpplatform.dto.admin;

import java.util.List;

/**
 * One row on the import review screen. {@code originalName} is what your phone had;
 * {@code suggestedName} is the cleaned version for you to accept or correct.
 */
public record ParsedContact(
        String originalName,
        String suggestedName,
        String phone,
        String phoneE164,
        /** True when the number couldn't be parsed — these can't be imported as-is. */
        boolean invalidPhone,
        /** Already in the database. */
        boolean alreadyExists,
        /** Other rows in this same file that look like the same person. */
        List<String> duplicateOf
) {}
