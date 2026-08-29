package com.wedding.rsvpplatform.dto.admin;

import com.wedding.rsvpplatform.model.MealPref;
import com.wedding.rsvpplatform.model.RsvpStatus;

public record AdminRsvpSummary(
        String eventKey,
        RsvpStatus status,
        int headcount,
        MealPref mealPref,
        String dietaryNotes
) {}
