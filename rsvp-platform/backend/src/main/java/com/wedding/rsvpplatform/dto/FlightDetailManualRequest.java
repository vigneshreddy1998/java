package com.wedding.rsvpplatform.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record FlightDetailManualRequest(
        @NotNull UUID guestId,
        String flightNumber,
        LocalDateTime arrivalDatetime,
        String airport,
        Boolean pickupNeeded
) {}
