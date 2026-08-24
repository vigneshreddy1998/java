package com.wedding.rsvpplatform.dto.admin;

import com.wedding.rsvpplatform.model.EventType;
import com.wedding.rsvpplatform.model.MealPref;
import com.wedding.rsvpplatform.model.RsvpStatus;

public record RsvpTrackerRow(
        String familyName,
        String guestName,
        EventType eventType,
        RsvpStatus status,
        MealPref mealPref,
        String dietaryNotes,
        String plusOneName,
        MealPref plusOneMealPref
) {}
