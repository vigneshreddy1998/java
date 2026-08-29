package com.wedding.rsvpplatform.controller;

import com.wedding.rsvpplatform.dto.*;
import com.wedding.rsvpplatform.service.GuestAccessService;
import com.wedding.rsvpplatform.service.GuestPortalService;
import com.wedding.rsvpplatform.security.GuestPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GuestController {

    private final GuestAccessService guestAccessService;
    private final GuestPortalService guestPortalService;

    public GuestController(GuestAccessService guestAccessService, GuestPortalService guestPortalService) {
        this.guestAccessService = guestAccessService;
        this.guestPortalService = guestPortalService;
    }

    /** The only unauthenticated guest endpoint: phone number in, session out. */
    @PostMapping("/verify")
    public VerifyResponse verify(@Valid @RequestBody VerifyRequest request, HttpServletRequest http) {
        return guestAccessService.verify(request.phone(), request.name(), clientKey(http));
    }

    @GetMapping("/me")
    public MeDto me(@AuthenticationPrincipal GuestPrincipal principal) {
        return guestPortalService.me(principal);
    }

    @GetMapping("/events/{eventKey}")
    public EventDto event(@AuthenticationPrincipal GuestPrincipal principal, @PathVariable String eventKey) {
        var e = guestPortalService.requireVisibleEvent(principal, eventKey);
        return new EventDto(e.getKey(), e.getName(), e.getDate(), e.getVenue(), e.getDressCode(),
                e.getColourTheme(), e.isCollectsRsvp(), e.isCollectsMeal(), e.isCollectsSongs(),
                e.getAccent(), e.getDisplayOrder());
    }

    @GetMapping("/events/{eventKey}/songs")
    public List<SongDto> songs(@AuthenticationPrincipal GuestPrincipal principal, @PathVariable String eventKey) {
        return guestPortalService.songsFor(principal, eventKey);
    }

    @PostMapping("/events/{eventKey}/rsvp")
    public RsvpDto submitRsvp(@AuthenticationPrincipal GuestPrincipal principal,
                               @PathVariable String eventKey,
                               @Valid @RequestBody RsvpSubmitRequest request) {
        return guestPortalService.submitRsvp(principal, eventKey, request);
    }

    /**
     * Rate-limit key. Prefers the forwarded address so a proxied deployment doesn't collapse
     * every visitor onto one bucket, which would lock the whole site out after five bad tries.
     */
    private String clientKey(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
