package com.wedding.rsvpplatform.controller;

import com.wedding.rsvpplatform.dto.SongClaimRequest;
import com.wedding.rsvpplatform.dto.SongDto;
import com.wedding.rsvpplatform.service.SongService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/songs")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping
    public List<SongDto> list() {
        return songService.listSongs();
    }

    @PostMapping("/{id}/claim")
    public SongDto claim(@PathVariable UUID id, @Valid @RequestBody SongClaimRequest request) {
        return songService.claim(id, request.familyInviteToken());
    }
}
