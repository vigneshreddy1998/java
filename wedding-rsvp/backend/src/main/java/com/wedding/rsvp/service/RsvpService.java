package com.wedding.rsvp.service;

import com.wedding.rsvp.dto.*;
import com.wedding.rsvp.model.Guest;
import com.wedding.rsvp.model.RsvpEntry;
import com.wedding.rsvp.repository.GuestRepository;
import com.wedding.rsvp.repository.RsvpRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RsvpService {

    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // no 0/O/1/I
    private final SecureRandom random = new SecureRandom();

    private final GuestRepository guestRepository;
    private final RsvpRepository rsvpRepository;

    public RsvpService(GuestRepository guestRepository, RsvpRepository rsvpRepository) {
        this.guestRepository = guestRepository;
        this.rsvpRepository = rsvpRepository;
    }

    public GuestLookupResponse lookupByCode(String code) {
        Guest guest = guestRepository.findByInviteCodeIgnoreCase(code)
                .orElseThrow(() -> new NoSuchElementException("We couldn't find an invitation with that code"));
        return new GuestLookupResponse(guest.getId(), guest.getPartyName(), guest.getMaxGuests(), guest.getEmail());
    }

    // Upserts by email (scoped to the event) so re-submitting an RSVP updates
    // the existing response instead of creating a duplicate. Flows that don't
    // collect an email (e.g. the Sangeeth invite) dedupe by name instead.
    public RsvpEntry submit(RsvpSubmissionRequest request) {
        if (request.getGuestId() != null) {
            Guest guest = guestRepository.findById(request.getGuestId())
                    .orElseThrow(() -> new NoSuchElementException("Invitation not found"));
            if (request.getGuestCount() > guest.getMaxGuests()) {
                throw new IllegalArgumentException(
                        "This invitation covers up to " + guest.getMaxGuests() + " guest(s)");
            }
        }

        String eventType = normalizeEventType(request.getEventType());
        String email = request.getEmail() == null || request.getEmail().isBlank() ? null : request.getEmail();

        RsvpEntry entry = (email != null
                ? rsvpRepository.findByEmailIgnoreCaseAndEventType(email, eventType)
                : rsvpRepository.findByGuestNameIgnoreCaseAndEventTypeAndEmailIsNull(request.getGuestName(), eventType))
                .orElseGet(RsvpEntry::new);

        entry.setEventType(eventType);
        entry.setGuestId(request.getGuestId());
        entry.setGuestName(request.getGuestName());
        entry.setEmail(email);
        entry.setAttending(Boolean.TRUE.equals(request.getAttending()));
        entry.setGuestCount(entry.isAttending() ? Math.max(1, request.getGuestCount()) : 0);
        entry.setMealSelections(request.getMealSelections() == null ? null
                : String.join(", ", request.getMealSelections()));
        entry.setAllergies(request.getAllergies());
        entry.setSongRequest(request.getSongRequest());
        entry.setMessage(request.getMessage());

        return rsvpRepository.save(entry);
    }

    public Optional<RsvpEntry> findExisting(String email, String eventType) {
        return rsvpRepository.findByEmailIgnoreCaseAndEventType(email, normalizeEventType(eventType));
    }

    private String normalizeEventType(String eventType) {
        return (eventType == null || eventType.isBlank()) ? "WEDDING" : eventType.trim().toUpperCase();
    }

    public RsvpSummary getSummary() {
        List<RsvpEntry> all = rsvpRepository.findAllByOrderBySubmittedAtDesc();
        int attendingParties = (int) all.stream().filter(RsvpEntry::isAttending).count();
        int decliningParties = all.size() - attendingParties;
        int totalGuests = all.stream().filter(RsvpEntry::isAttending)
                .mapToInt(RsvpEntry::getGuestCount).sum();
        return new RsvpSummary(all.size(), totalGuests, attendingParties, decliningParties, all);
    }

    public String exportCsv() {
        List<RsvpEntry> all = rsvpRepository.findAllByOrderBySubmittedAtDesc();
        StringBuilder sb = new StringBuilder();
        sb.append("Event,Name,Email,Attending,Guest Count,Meals,Allergies,Song Request,Message,Submitted At\n");
        for (RsvpEntry e : all) {
            sb.append(csv(e.getEventType())).append(',')
              .append(csv(e.getGuestName())).append(',')
              .append(csv(e.getEmail())).append(',')
              .append(e.isAttending() ? "Yes" : "No").append(',')
              .append(e.getGuestCount()).append(',')
              .append(csv(e.getMealSelections())).append(',')
              .append(csv(e.getAllergies())).append(',')
              .append(csv(e.getSongRequest())).append(',')
              .append(csv(e.getMessage())).append(',')
              .append(e.getSubmittedAt()).append('\n');
        }
        return sb.toString();
    }

    public Guest createGuest(GuestCreateRequest request) {
        String code = (request.getInviteCode() == null || request.getInviteCode().isBlank())
                ? generateUniqueCode()
                : request.getInviteCode().toUpperCase();
        Guest guest = new Guest(code, request.getPartyName(), request.getMaxGuests(),
                request.getEmail(), request.getPhone());
        return guestRepository.save(guest);
    }

    public List<Guest> listGuests() {
        return guestRepository.findAll();
    }

    private String generateUniqueCode() {
        String code;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                sb.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
            }
            code = sb.toString();
        } while (guestRepository.findByInviteCodeIgnoreCase(code).isPresent());
        return code;
    }

    private String csv(String value) {
        if (value == null) return "";
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    public static class NoSuchElementException extends RuntimeException {
        public NoSuchElementException(String message) { super(message); }
    }
}
