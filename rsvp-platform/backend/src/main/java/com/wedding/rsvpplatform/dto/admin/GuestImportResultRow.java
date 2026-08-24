package com.wedding.rsvpplatform.dto.admin;

public record GuestImportResultRow(
        String familyName,
        String inviteToken,
        String inviteLink
) {}
