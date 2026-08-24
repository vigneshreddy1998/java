package com.wedding.rsvpplatform.dto;

import jakarta.validation.constraints.NotBlank;

public record SongClaimRequest(
        @NotBlank String familyInviteToken
) {}
