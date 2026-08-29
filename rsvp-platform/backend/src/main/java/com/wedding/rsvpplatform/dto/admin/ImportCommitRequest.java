package com.wedding.rsvpplatform.dto.admin;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ImportCommitRequest(
        @NotNull List<Row> rows
) {
    public record Row(
            String name,
            String phone,
            /** Event keys this contact is invited to. */
            List<String> eventKeys
    ) {}
}
