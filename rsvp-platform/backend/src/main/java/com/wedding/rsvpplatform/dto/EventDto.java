package com.wedding.rsvpplatform.dto;

import java.time.LocalDateTime;

public record EventDto(
        String key,
        String name,
        LocalDateTime date,
        String venue,
        String dressCode,
        String colourTheme,
        boolean collectsRsvp,
        boolean collectsMeal,
        boolean collectsSongs,
        String accent,
        int displayOrder
) {}
