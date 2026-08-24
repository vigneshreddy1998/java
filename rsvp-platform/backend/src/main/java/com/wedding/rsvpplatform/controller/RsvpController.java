package com.wedding.rsvpplatform.controller;

import com.wedding.rsvpplatform.dto.RsvpDto;
import com.wedding.rsvpplatform.dto.RsvpSubmitRequest;
import com.wedding.rsvpplatform.service.RsvpService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rsvp")
public class RsvpController {

    private final RsvpService rsvpService;

    public RsvpController(RsvpService rsvpService) {
        this.rsvpService = rsvpService;
    }

    @PostMapping
    public RsvpDto submit(@Valid @RequestBody RsvpSubmitRequest request) {
        return rsvpService.submit(request);
    }
}
