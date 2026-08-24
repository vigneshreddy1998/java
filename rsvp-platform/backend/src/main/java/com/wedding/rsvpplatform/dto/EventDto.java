package com.wedding.rsvpplatform.dto;

import com.wedding.rsvpplatform.model.EventType;

import java.time.LocalDateTime;

public record EventDto(
        EventType type,
        String name,
        LocalDateTime date,
        String venue,
        String dressCode
) {}
