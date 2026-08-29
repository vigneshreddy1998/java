package com.wedding.rsvpplatform.dto;

import com.wedding.rsvpplatform.model.MealPref;
import com.wedding.rsvpplatform.model.RsvpStatus;

import java.util.List;

public record RsvpDto(
        String eventKey,
        RsvpStatus status,
        int headcount,
        MealPref mealPref,
        String dietaryNotes,
        List<CompanionDto> companions
) {}
