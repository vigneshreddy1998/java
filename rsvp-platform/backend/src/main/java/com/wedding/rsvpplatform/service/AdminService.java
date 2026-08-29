package com.wedding.rsvpplatform.service;

import com.wedding.rsvpplatform.dto.admin.*;
import com.wedding.rsvpplatform.exception.NotFoundException;
import com.wedding.rsvpplatform.model.*;
import com.wedding.rsvpplatform.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final GuestRepository guestRepository;
    private final EventRepository eventRepository;
    private final RsvpRepository rsvpRepository;
    private final GuestEventInviteRepository inviteRepository;
    private final SongRepository songRepository;
    private final SongPickRepository songPickRepository;
    private final PhoneNumberService phoneNumberService;
    private final ContactImportService contactImportService;

    public AdminService(GuestRepository guestRepository,
                         EventRepository eventRepository,
                         RsvpRepository rsvpRepository,
                         GuestEventInviteRepository inviteRepository,
                         SongRepository songRepository,
                         SongPickRepository songPickRepository,
                         PhoneNumberService phoneNumberService,
                         ContactImportService contactImportService) {
        this.guestRepository = guestRepository;
        this.eventRepository = eventRepository;
        this.rsvpRepository = rsvpRepository;
        this.inviteRepository = inviteRepository;
        this.songRepository = songRepository;
        this.songPickRepository = songPickRepository;
        this.phoneNumberService = phoneNumberService;
        this.contactImportService = contactImportService;
    }

    @Transactional(readOnly = true)
    public OverviewDto overview() {
        List<Guest> guests = guestRepository.findAll();
        List<Rsvp> allRsvps = rsvpRepository.findAllWithGuestAndEvent();

        List<OverviewDto.EventStats> stats = new ArrayList<>();
        for (Event event : eventRepository.findAllByOrderByDisplayOrderAsc()) {
            if (!event.isCollectsRsvp()) {
                continue;
            }
            List<Rsvp> forEvent = allRsvps.stream()
                    .filter(r -> r.getEvent().getId().equals(event.getId()))
                    .toList();

            long invited = event.isRequiresInvite()
                    ? inviteRepository.findAll().stream()
                        .filter(i -> i.getEvent().getId().equals(event.getId()))
                        .count()
                    : guests.size();

            long accepted = forEvent.stream().filter(r -> r.getStatus() == RsvpStatus.ACCEPTED).count();
            long declined = forEvent.stream().filter(r -> r.getStatus() == RsvpStatus.DECLINED).count();

            long headcount = forEvent.stream()
                    .filter(r -> r.getStatus() == RsvpStatus.ACCEPTED)
                    .mapToLong(Rsvp::getHeadcount)
                    .sum();

            Map<String, Long> meals = new LinkedHashMap<>();
            if (event.isCollectsMeal()) {
                for (Rsvp r : forEvent) {
                    if (r.getStatus() != RsvpStatus.ACCEPTED) {
                        continue;
                    }
                    String key = r.getMealPref() != null ? r.getMealPref().name() : "UNSPECIFIED";
                    meals.merge(key, 1L, Long::sum);
                }
            }

            stats.add(new OverviewDto.EventStats(event.getKey(), event.getName(),
                    accepted, declined, Math.max(0, invited - accepted - declined),
                    invited, headcount, meals));
        }

        int imported = (int) guests.stream().filter(g -> g.getSource() == GuestSource.IMPORTED).count();
        return new OverviewDto(stats, guests.size(), imported, guests.size() - imported);
    }

    @Transactional(readOnly = true)
    public List<AdminGuestRow> guests() {
        List<Rsvp> allRsvps = rsvpRepository.findAllWithGuestAndEvent();
        Map<UUID, List<Rsvp>> rsvpsByGuest = allRsvps.stream()
                .collect(Collectors.groupingBy(r -> r.getGuest().getId()));

        Map<UUID, List<String>> invitesByGuest = new HashMap<>();
        for (GuestEventInvite invite : inviteRepository.findAll()) {
            invitesByGuest.computeIfAbsent(invite.getGuest().getId(), k -> new ArrayList<>())
                    .add(invite.getEvent().getKey());
        }

        return guestRepository.findAll().stream()
                .map(g -> new AdminGuestRow(
                        g.getId(),
                        g.getName(),
                        phoneNumberService.forDisplay(g.getPhoneE164()),
                        g.getSource(),
                        invitesByGuest.getOrDefault(g.getId(), List.of()),
                        rsvpsByGuest.getOrDefault(g.getId(), List.of()).stream()
                                .map(r -> new AdminRsvpSummary(r.getEvent().getKey(), r.getStatus(),
                                        r.getHeadcount(), r.getMealPref(), r.getDietaryNotes()))
                                .toList()))
                .sorted(Comparator.comparing(row -> row.name() == null ? "￿" : row.name().toLowerCase()))
                .toList();
    }

    @Transactional
    public void updateInvites(UUID guestId, List<String> eventKeys) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new NotFoundException("Guest not found"));
        Map<String, Event> eventsByKey = new HashMap<>();
        eventRepository.findAll().forEach(e -> eventsByKey.put(e.getKey(), e));
        contactImportService.applyInvites(guest, eventKeys, eventsByKey);
    }

    /** Moves a self-registered guest onto your real list without touching their RSVP. */
    @Transactional
    public void promote(UUID guestId) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new NotFoundException("Guest not found"));
        guest.setSource(GuestSource.IMPORTED);
        guestRepository.save(guest);
    }

    @Transactional(readOnly = true)
    public List<SongAdminRow> songs() {
        Map<UUID, List<String>> pickersBySong = new HashMap<>();
        for (SongPick pick : songPickRepository.findAllWithGuestAndSong()) {
            String who = pick.getGuest().getName() != null
                    ? pick.getGuest().getName()
                    : phoneNumberService.forDisplay(pick.getGuest().getPhoneE164());
            pickersBySong.computeIfAbsent(pick.getSong().getId(), k -> new ArrayList<>()).add(who);
        }

        return songRepository.findAll().stream()
                .map(s -> {
                    List<String> pickedBy = pickersBySong.getOrDefault(s.getId(), List.of());
                    return new SongAdminRow(s.getId(), s.getTitle(), s.getPracticeVideoUrl(),
                            pickedBy, pickedBy.size() > 1);
                })
                .toList();
    }

    @Transactional
    public Song addSong(String title, String practiceVideoUrl) {
        return songRepository.save(Song.builder()
                .title(title)
                .practiceVideoUrl(practiceVideoUrl)
                .build());
    }

    @Transactional
    public void deleteSong(UUID songId) {
        songRepository.deleteById(songId);
    }

    @Transactional(readOnly = true)
    public List<Event> events() {
        return eventRepository.findAllByOrderByDisplayOrderAsc();
    }

    @Transactional
    public Event updateEvent(String key, EventUpdateRequest request) {
        Event event = eventRepository.findByKey(key)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        if (request.name() != null) event.setName(request.name());
        if (request.date() != null) event.setDate(request.date());
        if (request.venue() != null) event.setVenue(request.venue());
        if (request.dressCode() != null) event.setDressCode(request.dressCode());
        if (request.colourTheme() != null) event.setColourTheme(request.colourTheme());
        return eventRepository.save(event);
    }
}
