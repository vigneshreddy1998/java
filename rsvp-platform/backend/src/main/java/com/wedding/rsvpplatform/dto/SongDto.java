package com.wedding.rsvpplatform.dto;

import java.util.UUID;

public record SongDto(
        UUID id,
        String title,
        String practiceVideoUrl,
        /** Whether the currently verified guest has picked this one. */
        boolean pickedByMe
) {}
