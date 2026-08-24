package com.wedding.rsvpplatform.dto;

import java.util.List;
import java.util.UUID;

public record FamilyDto(
        UUID id,
        String displayName,
        String languagePref,
        String inviteToken,
        List<GuestDto> guests
) {}
