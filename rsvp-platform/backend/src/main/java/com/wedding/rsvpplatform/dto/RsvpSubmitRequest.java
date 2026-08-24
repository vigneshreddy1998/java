package com.wedding.rsvpplatform.dto;

import com.wedding.rsvpplatform.model.EventType;
import com.wedding.rsvpplatform.model.MealPref;
import com.wedding.rsvpplatform.model.RsvpStatus;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RsvpSubmitRequest(
        @NotNull UUID guestId,
        @NotNull EventType eventType,
        @NotNull RsvpStatus status,
        MealPref mealPref,
        String dietaryNotes,
        String plusOneName,
        MealPref plusOneMealPref
) {}
