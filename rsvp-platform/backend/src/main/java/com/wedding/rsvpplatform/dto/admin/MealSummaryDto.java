package com.wedding.rsvpplatform.dto.admin;

import java.util.List;
import java.util.Map;

public record MealSummaryDto(
        Map<String, Long> countsByMealPref,
        List<String> dietaryNotes
) {}
