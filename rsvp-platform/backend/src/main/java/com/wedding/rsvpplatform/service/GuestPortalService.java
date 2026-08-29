package com.wedding.rsvpplatform.service;

import com.wedding.rsvpplatform.dto.*;
import com.wedding.rsvpplatform.exception.ForbiddenException;
import com.wedding.rsvpplatform.exception.NotFoundException;
import com.wedding.rsvpplatform.model.*;
import com.wedding.rsvpplatform.repository.*;
import com.wedding.rsvpplatform.security.GuestPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/** Everything a verified guest can read or change about their own attendance. */
@Service
public class GuestPortalService {

    private final GuestRepository guestRepository;
    private final EventRepository eventRepository;
    private final RsvpRepository rsvpRepository;
    private final CompanionRepository companionRepository;
    private final SongRepository songRepository;
    private final SongPickRepository songPickRepository;
    private final PhoneNumberService phoneNumberService;

    public GuestPortalService(GuestRepository guestRepository,
                               EventRepository eventRepository,
                               RsvpRepository rsvpRepository,
                               CompanionRepository companionRepository,
                               SongRepository songRepository,
                               SongPickRepository songPickRepository,
                               PhoneNumberService phoneNumberService) {
        this.guestRepository = guestRepository;
        this.eventRepository = eventRepository;
        this.rsvpRepository = rsvpRepository;
        this.companionRepository = companionRepository;
        this.songRepository = songRepository;
        this.songPickRepository = songPickRepository;
        this.phoneNumberService = phoneNumberService;
    }

    @Transactional(readOnly = true)
    public MeDto me(GuestPrincipal principal) {
        Guest guest = requireGuest(principal);

        List<EventDto> events = visibleEvents(principal).stream()
                .map(this::toEventDto)
                .toList();

        List<RsvpDto> rsvps = rsvpRepository.findByGuestId(guest.getId()).stream()
                // A guest could hold an old RSVP for an event they can no longer see, if you
                // untick an invite. Filter by the session so nothing leaks back through here.
                .filter(r -> principal.canSee(r.getEvent().getKey()))
                .map(this::toRsvpDto)
                .toList();

        return new MeDto(guest.getName(), guest.isWhatsappConsent(), events, rsvps);
    }

    /**
     * The authoritative visibility check. Reads the event list from the signed session, never
     * from anything the caller supplied, so typing a URL for a private event gets a 404 rather
     * than the event.
     */
    @Transactional(readOnly = true)
    public Event requireVisibleEvent(GuestPrincipal principal, String eventKey) {
        if (!principal.canSee(eventKey)) {
            // Deliberately "not found", not "forbidden" — a gated event must not confirm its
            // own existence to someone who wasn't invited.
            throw new NotFoundException("Event not found");
        }
        return eventRepository.findByKey(eventKey)
                .orElseThrow(() -> new NotFoundException("Event not found"));
    }

    @Transactional
    public RsvpDto submitRsvp(GuestPrincipal principal, String eventKey, RsvpSubmitRequest request) {
        Guest guest = requireGuest(principal);
        Event event = requireVisibleEvent(principal, eventKey);

        if (!event.isCollectsRsvp()) {
            throw new ForbiddenException("This event doesn't take RSVPs.");
        }

        if (request.guestName() != null && !request.guestName().isBlank()) {
            guest.setName(request.guestName().trim());
        }
        if (request.whatsappConsent() != null) {
            guest.setWhatsappConsent(request.whatsappConsent());
        }
        guestRepository.save(guest);

        Rsvp rsvp = rsvpRepository.findByGuestIdAndEventId(guest.getId(), event.getId())
                .orElseGet(() -> Rsvp.builder().guest(guest).event(event).build());

        rsvp.setStatus(request.status());
        rsvp.setHeadcount(request.status() == RsvpStatus.ACCEPTED ? request.headcount() : 0);
        rsvp.setMealPref(event.isCollectsMeal() ? request.mealPref() : null);
        rsvp.setDietaryNotes(event.isCollectsMeal() ? request.dietaryNotes() : null);
        rsvpRepository.save(rsvp);

        replaceCompanions(guest, event, request);
        if (event.isCollectsSongs()) {
            replaceSongPicks(guest, request.songIds(), request.status());
        }

        return toRsvpDto(rsvp);
    }

    /** Companions are replaced wholesale so an edited RSVP doesn't accumulate stale rows. */
    private void replaceCompanions(Guest guest, Event event, RsvpSubmitRequest request) {
        companionRepository.deleteAllForGuestAndEvent(guest.getId(), event.getId());
        if (request.status() != RsvpStatus.ACCEPTED || request.companions() == null) {
            return;
        }
        for (CompanionDto c : request.companions()) {
            boolean hasName = c.name() != null && !c.name().isBlank();
            boolean hasPhone = c.phone() != null && !c.phone().isBlank();
            if (!hasName && !hasPhone) {
                continue;
            }
            companionRepository.save(Companion.builder()
                    .guest(guest)
                    .event(event)
                    .name(hasName ? c.name().trim() : null)
                    .phoneE164(hasPhone ? phoneNumberService.toE164(c.phone()).orElse(null) : null)
                    .build());
        }
    }

    private void replaceSongPicks(Guest guest, List<String> songIds, RsvpStatus status) {
        songPickRepository.deleteAllForGuest(guest.getId());
        if (status != RsvpStatus.ACCEPTED || songIds == null) {
            return;
        }
        for (String rawId : songIds) {
            // A song removed by the admin while a guest had the form open would otherwise
            // fail their whole RSVP. Skip anything unrecognisable rather than rejecting.
            UUID songId;
            try {
                songId = UUID.fromString(rawId);
            } catch (IllegalArgumentException e) {
                continue;
            }
            songRepository.findById(songId).ifPresent(song ->
                    songPickRepository.save(SongPick.builder().guest(guest).song(song).build()));
        }
    }

    @Transactional(readOnly = true)
    public List<SongDto> songsFor(GuestPrincipal principal, String eventKey) {
        Event event = requireVisibleEvent(principal, eventKey);
        if (!event.isCollectsSongs()) {
            throw new NotFoundException("Event not found");
        }
        Set<UUID> mine = songPickRepository.findByGuestId(principal.guestId()).stream()
                .map(p -> p.getSong().getId())
                .collect(Collectors.toSet());

        return songRepository.findAll().stream()
                .map(s -> new SongDto(s.getId(), s.getTitle(), s.getPracticeVideoUrl(), mine.contains(s.getId())))
                .toList();
    }

    private List<Event> visibleEvents(GuestPrincipal principal) {
        List<Event> visible = new ArrayList<>();
        for (Event event : eventRepository.findAllByOrderByDisplayOrderAsc()) {
            if (principal.canSee(event.getKey())) {
                visible.add(event);
            }
        }
        return visible;
    }

    private Guest requireGuest(GuestPrincipal principal) {
        return guestRepository.findById(principal.guestId())
                .orElseThrow(() -> new NotFoundException("Guest not found"));
    }

    private EventDto toEventDto(Event e) {
        return new EventDto(e.getKey(), e.getName(), e.getDate(), e.getVenue(), e.getDressCode(),
                e.getColourTheme(), e.isCollectsRsvp(), e.isCollectsMeal(), e.isCollectsSongs(),
                e.getAccent(), e.getDisplayOrder());
    }

    private RsvpDto toRsvpDto(Rsvp r) {
        List<CompanionDto> companions = companionRepository
                .findByGuestIdAndEventId(r.getGuest().getId(), r.getEvent().getId()).stream()
                .map(c -> new CompanionDto(c.getName(), c.getPhoneE164()))
                .toList();
        return new RsvpDto(r.getEvent().getKey(), r.getStatus(), r.getHeadcount(),
                r.getMealPref(), r.getDietaryNotes(), companions);
    }

    /** Used by the admin promote flow to check a guest exists. */
    @Transactional(readOnly = true)
    public Optional<Guest> findGuest(UUID id) {
        return guestRepository.findById(id);
    }
}
