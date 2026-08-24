package com.wedding.rsvpplatform.dto;

import java.util.UUID;

public record SongDto(
        UUID id,
        String title,
        String practiceVideoUrl,
        boolean locked,
        String claimedByFamilyName
) {}
