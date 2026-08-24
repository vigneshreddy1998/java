package com.wedding.rsvpplatform.controller;

import com.wedding.rsvpplatform.config.AppProperties;
import com.wedding.rsvpplatform.dto.SongCreateRequest;
import com.wedding.rsvpplatform.dto.SongDto;
import com.wedding.rsvpplatform.dto.admin.*;
import com.wedding.rsvpplatform.model.EventType;
import com.wedding.rsvpplatform.model.RsvpStatus;
import com.wedding.rsvpplatform.service.AdminService;
import com.wedding.rsvpplatform.service.SongService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final SongService songService;
    private final AppProperties appProperties;

    public AdminController(AdminService adminService, SongService songService, AppProperties appProperties) {
        this.adminService = adminService;
        this.songService = songService;
        this.appProperties = appProperties;
    }

    @GetMapping("/rsvps")
    public List<RsvpTrackerRow> tracker(@RequestParam(required = false) EventType eventType,
                                         @RequestParam(required = false) RsvpStatus status) {
        return adminService.tracker(eventType, status);
    }

    @GetMapping("/meals-summary")
    public MealSummaryDto mealSummary(@RequestParam EventType eventType) {
        return adminService.mealSummary(eventType);
    }

    @GetMapping("/logistics")
    public List<LogisticsRow> logistics() {
        return adminService.logistics();
    }

    @GetMapping("/non-responders")
    public List<NonResponderRow> nonResponders() {
        return adminService.nonResponders();
    }

    @PostMapping("/reminders/send")
    public Map<String, Integer> sendReminders() {
        return Map.of("remindersQueued", adminService.sendReminders());
    }

    @PostMapping("/songs")
    public SongDto createSong(@Valid @RequestBody SongCreateRequest request) {
        return songService.create(request);
    }

    @PutMapping("/events/{type}")
    public AdminService.EventDtoAdmin updateEvent(@PathVariable EventType type,
                                                    @RequestBody EventUpdateRequest request) {
        return adminService.updateEvent(type, request);
    }

    @PostMapping(value = "/guests/import", consumes = "multipart/form-data")
    public List<GuestImportResultRow> importGuests(@RequestParam("file") MultipartFile file) throws IOException {
        List<AdminService.GuestImportRow> rows = parseCsv(file);
        return adminService.importGuests(rows, appProperties.frontendOrigin());
    }

    private List<AdminService.GuestImportRow> parseCsv(MultipartFile file) throws IOException {
        List<AdminService.GuestImportRow> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String header = reader.readLine();
            if (header == null) return rows;

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] cols = line.split(",", -1);
                String familyName = cols.length > 0 ? cols[0].trim() : null;
                String guestName = cols.length > 1 ? cols[1].trim() : null;
                String mealPref = cols.length > 2 ? cols[2].trim() : null;
                String languagePref = cols.length > 3 ? cols[3].trim() : null;
                rows.add(new AdminService.GuestImportRow(familyName, guestName, mealPref, languagePref));
            }
        }
        return rows;
    }
}
