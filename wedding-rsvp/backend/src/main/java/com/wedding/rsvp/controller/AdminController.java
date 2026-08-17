package com.wedding.rsvp.controller;

import com.wedding.rsvp.dto.GuestCreateRequest;
import com.wedding.rsvp.dto.RsvpSummary;
import com.wedding.rsvp.model.Guest;
import com.wedding.rsvp.service.RsvpService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final RsvpService rsvpService;
    private final String adminKey;

    public AdminController(RsvpService rsvpService, @Value("${app.admin-key}") String adminKey) {
        this.rsvpService = rsvpService;
        this.adminKey = adminKey;
    }

    private boolean isAuthorized(String providedKey) {
        return adminKey != null && !adminKey.isBlank() && adminKey.equals(providedKey);
    }

    @GetMapping("/rsvps")
    public ResponseEntity<RsvpSummary> getAllRsvps(@RequestHeader("X-Admin-Key") String key) {
        if (!isAuthorized(key)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(rsvpService.getSummary());
    }

    @GetMapping("/export")
    public ResponseEntity<String> exportCsv(@RequestHeader("X-Admin-Key") String key) {
        if (!isAuthorized(key)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String csv = rsvpService.exportCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"rsvps.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @GetMapping("/guests")
    public ResponseEntity<List<Guest>> listGuests(@RequestHeader("X-Admin-Key") String key) {
        if (!isAuthorized(key)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(rsvpService.listGuests());
    }

    @PostMapping("/guests")
    public ResponseEntity<Guest> addGuest(@RequestHeader("X-Admin-Key") String key,
                                           @Valid @RequestBody GuestCreateRequest request) {
        if (!isAuthorized(key)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(rsvpService.createGuest(request));
    }

    @PostMapping("/guests/bulk")
    public ResponseEntity<List<Guest>> addGuests(@RequestHeader("X-Admin-Key") String key,
                                                  @RequestBody List<GuestCreateRequest> requests) {
        if (!isAuthorized(key)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(requests.stream().map(rsvpService::createGuest).toList());
    }
}
