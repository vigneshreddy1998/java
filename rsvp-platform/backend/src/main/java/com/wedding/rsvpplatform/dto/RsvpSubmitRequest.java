package com.wedding.rsvpplatform.dto;

import com.wedding.rsvpplatform.model.MealPref;
import com.wedding.rsvpplatform.model.RsvpStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RsvpSubmitRequest(
        @NotNull RsvpStatus status,
        @Min(1) @Max(20) int headcount,
        MealPref mealPref,
        String dietaryNotes,
        List<CompanionDto> companions,
        /** Song ids, only meaningful for an event that collects them. */
        List<String> songIds,
        Boolean whatsappConsent,
        /** Lets a self-registered guest tell us who they are. */
        String guestName
) {}
