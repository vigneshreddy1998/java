package com.wedding.rsvpplatform.service;

import com.wedding.rsvpplatform.dto.admin.*;
import com.wedding.rsvpplatform.exception.NotFoundException;
import com.wedding.rsvpplatform.model.*;
import com.wedding.rsvpplatform.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    private final RsvpRepository rsvpRepository;
    private final GuestRepository guestRepository;
    private final FamilyRepository familyRepository;
    private final WeddingEventRepository eventRepository;
    private final FlightDetailRepository flightDetailRepository;

    public AdminService(RsvpRepository rsvpRepository, GuestRepository guestRepository,
                         FamilyRepository familyRepository, WeddingEventRepository eventRepository,
                         FlightDetailRepository flightDetailRepository) {
        this.rsvpRepository = rsvpRepository;
        this.guestRepository = guestRepository;
        this.familyRepository = familyRepository;
        this.eventRepository = eventRepository;
        this.flightDetailRepository = flightDetailRepository;
    }

    @Transactional(readOnly = true)
    public List<RsvpTrackerRow> tracker(EventType eventType, RsvpStatus status) {
        List<Rsvp> rsvps = eventType != null
                ? (status != null ? rsvpRepository.findByEventTypeAndStatus(eventType, status)
                                   : rsvpRepository.findByEventType(eventType))
                : rsvpRepository.findAll();

        return rsvps.stream()
                .filter(r -> status == null || r.getStatus() == status)
                .map(r -> new RsvpTrackerRow(
                        r.getGuest().getFamily().getDisplayName(),
                        r.getGuest().getName(),
                        r.getEvent().getType(),
                        r.getStatus(),
                        r.getGuest().getMealPref(),
                        r.getGuest().getDietaryNotes(),
                        r.getPlusOneName(),
                        r.getPlusOneMealPref()))
                .toList();
    }

    @Transactional(readOnly = true)
    public MealSummaryDto mealSummary(EventType eventType) {
        List<Rsvp> accepted = rsvpRepository.findByEventTypeAndStatus(eventType, RsvpStatus.ACCEPTED);

        Map<String, Long> counts = new LinkedHashMap<>();
        List<String> dietaryNotes = new ArrayList<>();

        for (Rsvp r : accepted) {
            Guest g = r.getGuest();
            String meal = g.getMealPref() != null ? g.getMealPref().name() : "UNSPECIFIED";
            counts.merge(meal, 1L, Long::sum);
            if (g.getDietaryNotes() != null && !g.getDietaryNotes().isBlank()) {
                dietaryNotes.add(g.getName() + ": " + g.getDietaryNotes());
            }
            if (r.getPlusOneName() != null && !r.getPlusOneName().isBlank()) {
                String plusOneMeal = r.getPlusOneMealPref() != null ? r.getPlusOneMealPref().name() : "UNSPECIFIED";
                counts.merge(plusOneMeal, 1L, Long::sum);
            }
        }

        return new MealSummaryDto(counts, dietaryNotes);
    }

    @Transactional(readOnly = true)
    public List<LogisticsRow> logistics() {
        return flightDetailRepository.findAllByOrderByArrivalDatetimeAsc().stream()
                .map(d -> new LogisticsRow(
                        d.getGuest().getFamily().getDisplayName(),
                        d.getGuest().getName(),
                        d.getFlightNumber(),
                        d.getArrivalDatetime(),
                        d.getAirport(),
                        d.getPickupNeeded()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NonResponderRow> nonResponders() {
        List<Guest> allGuests = guestRepository.findAll();
        List<WeddingEvent> events = eventRepository.findAll();
        List<NonResponderRow> result = new ArrayList<>();

        for (WeddingEvent event : events) {
            Set<UUID> respondedGuestIds = rsvpRepository.findByEventType(event.getType()).stream()
                    .filter(r -> r.getStatus() != RsvpStatus.PENDING)
                    .map(r -> r.getGuest().getId())
                    .collect(Collectors.toSet());

            for (Guest guest : allGuests) {
                if (!respondedGuestIds.contains(guest.getId())) {
                    result.add(new NonResponderRow(guest.getId(), guest.getFamily().getDisplayName(),
                            guest.getName(), event.getType()));
                }
            }
        }
        return result;
    }

    public int sendReminders() {
        List<NonResponderRow> nonResponders = nonResponders();
        // No SMS/email provider is wired up yet — this logs the batch so an admin can see who's
        // outstanding. Wire an actual channel here (email/SMS) when one is chosen.
        log.info("Reminder batch: {} outstanding RSVP(s) to nudge: {}", nonResponders.size(),
                nonResponders.stream().map(NonResponderRow::guestName).toList());
        return nonResponders.size();
    }

    @Transactional
    public List<GuestImportResultRow> importGuests(List<GuestImportRow> rows, String frontendOrigin) {
        Map<String, Family> familiesByName = new LinkedHashMap<>();
        List<GuestImportResultRow> results = new ArrayList<>();

        for (GuestImportRow row : rows) {
            if (row.familyName() == null || row.familyName().isBlank() ||
                row.guestName() == null || row.guestName().isBlank()) {
                continue;
            }

            Family family = familiesByName.computeIfAbsent(row.familyName(), name ->
                    familyRepository.findByDisplayName(name).orElseGet(() ->
                            familyRepository.save(Family.builder()
                                    .displayName(name)
                                    .languagePref(row.languagePref() != null ? row.languagePref() : "en")
                                    .build())));

            MealPref mealPref = parseMealPref(row.mealPref());
            Guest guest = Guest.builder()
                    .family(family)
                    .name(row.guestName())
                    .mealPref(mealPref)
                    .build();
            guestRepository.save(guest);
        }

        for (Family family : familiesByName.values()) {
            String link = frontendOrigin + "/rsvp/" + family.getInviteToken();
            results.add(new GuestImportResultRow(family.getDisplayName(), family.getInviteToken(), link));
        }
        return results;
    }

    private MealPref parseMealPref(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            return MealPref.valueOf(raw.trim().toUpperCase().replace(' ', '_'));
        } catch (IllegalArgumentException e) {
            return MealPref.OTHER;
        }
    }

    @Transactional
    public EventDtoAdmin updateEvent(EventType type, EventUpdateRequest request) {
        WeddingEvent event = eventRepository.findByType(type)
                .orElseThrow(() -> new NotFoundException("Event not found"));
        if (request.name() != null) event.setName(request.name());
        if (request.date() != null) event.setDate(request.date());
        if (request.venue() != null) event.setVenue(request.venue());
        if (request.dressCode() != null) event.setDressCode(request.dressCode());
        WeddingEvent saved = eventRepository.save(event);
        return new EventDtoAdmin(saved.getType(), saved.getName(), saved.getDate(), saved.getVenue(), saved.getDressCode());
    }

    public record EventDtoAdmin(EventType type, String name, java.time.LocalDateTime date, String venue, String dressCode) {}
    public record GuestImportRow(String familyName, String guestName, String mealPref, String languagePref) {}
}
