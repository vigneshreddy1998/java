package com.wedding.rsvpplatform.dto.admin;

import com.wedding.rsvpplatform.model.EventType;

import java.util.UUID;

public record NonResponderRow(
        UUID guestId,
        String familyName,
        String guestName,
        EventType eventType
) {}
