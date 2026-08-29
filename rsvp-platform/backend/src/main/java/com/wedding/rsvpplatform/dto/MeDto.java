package com.wedding.rsvpplatform.dto;

import java.util.List;

/** Everything the front end needs after verifying: who you are, and what you may see. */
public record MeDto(
        String name,
        boolean whatsappConsent,
        List<EventDto> events,
        List<RsvpDto> rsvps
) {}
