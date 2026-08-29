package com.wedding.rsvpplatform.dto.admin;

import java.time.LocalDateTime;

public record EventUpdateRequest(
        String name,
        LocalDateTime date,
        String venue,
        String dressCode,
        String colourTheme
) {}
