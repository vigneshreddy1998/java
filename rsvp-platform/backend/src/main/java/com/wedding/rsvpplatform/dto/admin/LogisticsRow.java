package com.wedding.rsvpplatform.dto.admin;

import java.time.LocalDateTime;

public record LogisticsRow(
        String familyName,
        String guestName,
        String flightNumber,
        LocalDateTime arrivalDatetime,
        String airport,
        Boolean pickupNeeded
) {}
