package com.wedding.rsvpplatform.dto;

import jakarta.validation.constraints.NotBlank;

public record VerifyRequest(
        @NotBlank String phone,
        /** Optional — captured for a guest who isn't already on the list. */
        String name
) {}
