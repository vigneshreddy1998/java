package com.wedding.rsvpplatform.dto.admin;

import java.util.List;
import java.util.UUID;

public record SongAdminRow(
        UUID id,
        String title,
        String practiceVideoUrl,
        List<String> pickedBy,
        /** True when more than one guest is preparing this — surfaced before the night. */
        boolean duplicate
) {}
