package com.wedding.rsvpplatform.dto.admin;

import com.wedding.rsvpplatform.model.GuestSource;

import java.util.List;
import java.util.UUID;

public record AdminGuestRow(
        UUID id,
        String name,
        String phone,
        GuestSource source,
        List<String> invitedEventKeys,
        List<AdminRsvpSummary> rsvps
) {}
