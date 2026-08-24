package com.wedding.rsvpplatform.dto;

import com.wedding.rsvpplatform.model.EventType;
import com.wedding.rsvpplatform.model.MealPref;
import com.wedding.rsvpplatform.model.RsvpStatus;

public record RsvpDto(
        EventType eventType,
        RsvpStatus status,
        String plusOneName,
        MealPref plusOneMealPref
) {}
