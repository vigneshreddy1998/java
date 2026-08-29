package com.wedding.rsvpplatform.dto.admin;

import java.util.List;
import java.util.Map;

public record OverviewDto(
        List<EventStats> events,
        int totalGuests,
        int importedGuests,
        int selfRegisteredGuests
) {
    public record EventStats(
            String eventKey,
            String eventName,
            long accepted,
            long declined,
            long pending,
            long invited,
            /** Sum of headcount across accepted RSVPs — the number the caterer needs. */
            long headcount,
            Map<String, Long> mealCounts
    ) {}
}
