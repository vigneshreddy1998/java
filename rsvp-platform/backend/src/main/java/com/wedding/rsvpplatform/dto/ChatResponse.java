package com.wedding.rsvpplatform.dto;

public record ChatResponse(
        boolean complete,
        String reply,
        FlightDetailDto extracted
) {}
