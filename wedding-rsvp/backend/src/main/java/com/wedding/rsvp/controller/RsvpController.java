package com.wedding.rsvp.controller;

import com.wedding.rsvp.dto.GuestLookupResponse;
import com.wedding.rsvp.dto.RsvpSubmissionRequest;
import com.wedding.rsvp.model.RsvpEntry;
import com.wedding.rsvp.service.RsvpService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class RsvpController {

    private final RsvpService rsvpService;

    public RsvpController(RsvpService rsvpService) {
        this.rsvpService = rsvpService;
    }

    @GetMapping("/guests/lookup")
    public ResponseEntity<GuestLookupResponse> lookup(@RequestParam String code) {
        return ResponseEntity.ok(rsvpService.lookupByCode(code));
    }

    @PostMapping("/rsvp")
    public ResponseEntity<RsvpEntry> submit(@Valid @RequestBody RsvpSubmissionRequest request) {
        return ResponseEntity.ok(rsvpService.submit(request));
    }

    @GetMapping("/rsvp/check")
    public ResponseEntity<?> check(@RequestParam String email,
                                    @RequestParam(required = false) String eventType) {
        Optional<RsvpEntry> existing = rsvpService.findExisting(email, eventType);
        return existing.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok(Map.of("found", false)));
    }
}
