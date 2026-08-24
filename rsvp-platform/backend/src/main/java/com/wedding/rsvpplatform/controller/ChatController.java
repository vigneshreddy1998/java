package com.wedding.rsvpplatform.controller;

import com.wedding.rsvpplatform.dto.ChatRequest;
import com.wedding.rsvpplatform.dto.ChatResponse;
import com.wedding.rsvpplatform.service.FlightExtractionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final FlightExtractionService flightExtractionService;

    public ChatController(FlightExtractionService flightExtractionService) {
        this.flightExtractionService = flightExtractionService;
    }

    @PostMapping("/flight-details")
    public ChatResponse flightDetails(@Valid @RequestBody ChatRequest request) {
        return flightExtractionService.converse(request.guestId(), request.messages());
    }
}
