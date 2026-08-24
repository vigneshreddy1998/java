package com.wedding.rsvpplatform.dto;

import jakarta.validation.constraints.NotBlank;

public record SongCreateRequest(
        @NotBlank String title,
        String practiceVideoUrl
) {}
