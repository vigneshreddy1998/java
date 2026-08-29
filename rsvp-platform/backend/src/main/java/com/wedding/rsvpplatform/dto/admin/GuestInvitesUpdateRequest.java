package com.wedding.rsvpplatform.dto.admin;

import jakarta.validation.constraints.NotNull;

import java.util.List;

/** The full set of event keys this guest should be invited to — replaces what's stored. */
public record GuestInvitesUpdateRequest(
        @NotNull List<String> eventKeys
) {}
