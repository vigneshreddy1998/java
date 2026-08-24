package com.wedding.rsvpplatform.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record FlightDetailDto(
        UUID guestId,
        String flightNumber,
        LocalDateTime arrivalDatetime,
        String airport,
        Boolean pickupNeeded
) {}
