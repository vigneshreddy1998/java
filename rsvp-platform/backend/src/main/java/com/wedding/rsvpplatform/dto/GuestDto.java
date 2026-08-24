package com.wedding.rsvpplatform.dto;

import com.wedding.rsvpplatform.model.MealPref;

import java.util.List;
import java.util.UUID;

public record GuestDto(
        UUID id,
        String name,
        MealPref mealPref,
        String dietaryNotes,
        List<RsvpDto> rsvps
) {}
