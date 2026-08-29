package com.wedding.rsvpplatform.controller;

import com.wedding.rsvpplatform.dto.EventDto;
import com.wedding.rsvpplatform.dto.SongCreateRequest;
import com.wedding.rsvpplatform.dto.admin.*;
import com.wedding.rsvpplatform.model.Event;
import com.wedding.rsvpplatform.service.AdminService;
import com.wedding.rsvpplatform.service.ContactImportService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final ContactImportService contactImportService;

    public AdminController(AdminService adminService, ContactImportService contactImportService) {
        this.adminService = adminService;
        this.contactImportService = contactImportService;
    }

    @GetMapping("/overview")
    public OverviewDto overview() {
        return adminService.overview();
    }

    @GetMapping("/guests")
    public List<AdminGuestRow> guests() {
        return adminService.guests();
    }

    @PutMapping("/guests/{id}/invites")
    public Map<String, String> updateInvites(@PathVariable UUID id,
                                              @Valid @RequestBody GuestInvitesUpdateRequest request) {
        adminService.updateInvites(id, request.eventKeys());
        return Map.of("status", "updated");
    }

    @PostMapping("/guests/{id}/promote")
    public Map<String, String> promote(@PathVariable UUID id) {
        adminService.promote(id);
        return Map.of("status", "promoted");
    }

    /** Step one of import: parse and propose. Nothing is saved here. */
    @PostMapping(value = "/contacts/preview", consumes = "multipart/form-data")
    public ImportPreview previewImport(@RequestParam("file") MultipartFile file) throws IOException {
        return contactImportService.preview(file);
    }

    /** Step two: save exactly the rows the admin approved. */
    @PostMapping("/contacts/commit")
    public Map<String, Integer> commitImport(@Valid @RequestBody ImportCommitRequest request) {
        return Map.of("imported", contactImportService.commit(request));
    }

    @GetMapping("/songs")
    public List<SongAdminRow> songs() {
        return adminService.songs();
    }

    @PostMapping("/songs")
    public Map<String, String> addSong(@Valid @RequestBody SongCreateRequest request) {
        var song = adminService.addSong(request.title(), request.practiceVideoUrl());
        return Map.of("id", song.getId().toString());
    }

    @DeleteMapping("/songs/{id}")
    public Map<String, String> deleteSong(@PathVariable UUID id) {
        adminService.deleteSong(id);
        return Map.of("status", "deleted");
    }

    @GetMapping("/events")
    public List<EventDto> events() {
        return adminService.events().stream().map(AdminController::toDto).toList();
    }

    @PutMapping("/events/{key}")
    public EventDto updateEvent(@PathVariable String key, @RequestBody EventUpdateRequest request) {
        return toDto(adminService.updateEvent(key, request));
    }

    private static EventDto toDto(Event e) {
        return new EventDto(e.getKey(), e.getName(), e.getDate(), e.getVenue(), e.getDressCode(),
                e.getColourTheme(), e.isCollectsRsvp(), e.isCollectsMeal(), e.isCollectsSongs(),
                e.getAccent(), e.getDisplayOrder());
    }
}
