package com.wedding.rsvpplatform.service;

import com.wedding.rsvpplatform.dto.admin.ImportCommitRequest;
import com.wedding.rsvpplatform.dto.admin.ImportPreview;
import com.wedding.rsvpplatform.dto.admin.ParsedContact;
import com.wedding.rsvpplatform.model.Event;
import com.wedding.rsvpplatform.model.Guest;
import com.wedding.rsvpplatform.model.GuestEventInvite;
import com.wedding.rsvpplatform.model.GuestSource;
import com.wedding.rsvpplatform.repository.EventRepository;
import com.wedding.rsvpplatform.repository.GuestEventInviteRepository;
import com.wedding.rsvpplatform.repository.GuestRepository;
import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.property.Telephone;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Reads a .vcf export from a phone's contacts app and turns it into reviewable rows.
 *
 * <p>Import is two steps on purpose: <b>preview</b> parses and proposes, <b>commit</b> saves
 * exactly the rows you approved. Nothing reaches the database from a file upload alone.
 */
@Service
public class ContactImportService {

    private final GuestRepository guestRepository;
    private final EventRepository eventRepository;
    private final GuestEventInviteRepository inviteRepository;
    private final PhoneNumberService phoneNumberService;
    private final ContactCleanupService cleanupService;

    public ContactImportService(GuestRepository guestRepository,
                                 EventRepository eventRepository,
                                 GuestEventInviteRepository inviteRepository,
                                 PhoneNumberService phoneNumberService,
                                 ContactCleanupService cleanupService) {
        this.guestRepository = guestRepository;
        this.eventRepository = eventRepository;
        this.inviteRepository = inviteRepository;
        this.phoneNumberService = phoneNumberService;
        this.cleanupService = cleanupService;
    }

    public ImportPreview preview(MultipartFile file) throws IOException {
        record Raw(String name, String phone) {}
        List<Raw> raws = new ArrayList<>();

        try (InputStream in = file.getInputStream()) {
            for (VCard card : Ezvcard.parse(in).all()) {
                String name = card.getFormattedName() != null
                        ? card.getFormattedName().getValue()
                        : null;
                // A contact with several numbers becomes several candidate rows; you decide
                // which one is the person's actual mobile on the review screen.
                for (Telephone tel : card.getTelephoneNumbers()) {
                    String number = tel.getText() != null ? tel.getText()
                            : (tel.getUri() != null ? tel.getUri().getNumber() : null);
                    if (number != null && !number.isBlank()) {
                        raws.add(new Raw(name, number));
                    }
                }
            }
        }

        Map<Integer, String> suggestions = cleanupService.suggestNames(
                raws.stream().map(Raw::name).toList());

        boolean cleanupApplied = !suggestions.isEmpty();
        String cleanupNote = cleanupApplied
                ? "Names cleaned up — review the suggestions before importing."
                : (cleanupService.isEnabled()
                        ? "Name cleanup was unavailable, so names are shown exactly as stored."
                        : "Name cleanup is off (no API key configured), so names are shown as stored.");

        // Group by canonical number so the same person saved twice collapses into one warning.
        Map<String, List<Integer>> byNumber = new LinkedHashMap<>();
        List<ParsedContact> contacts = new ArrayList<>();
        int invalid = 0;

        for (int i = 0; i < raws.size(); i++) {
            Raw raw = raws.get(i);
            Optional<String> e164 = phoneNumberService.toE164(raw.phone());
            String suggested = suggestions.getOrDefault(i, null);
            String finalName = (suggested != null && !suggested.isBlank())
                    ? suggested
                    : (raw.name() == null ? "" : raw.name().trim());

            if (e164.isEmpty()) {
                invalid++;
                contacts.add(new ParsedContact(raw.name(), finalName, raw.phone(), null,
                        true, false, List.of()));
                continue;
            }

            byNumber.computeIfAbsent(e164.get(), k -> new ArrayList<>()).add(contacts.size());
            boolean exists = guestRepository.findByPhoneE164(e164.get()).isPresent();
            contacts.add(new ParsedContact(raw.name(), finalName, raw.phone(), e164.get(),
                    false, exists, List.of()));
        }

        // Second pass: annotate rows that share a number with another row in this file.
        int duplicates = 0;
        Map<Integer, List<String>> dupeLabels = new HashMap<>();
        for (List<Integer> group : byNumber.values()) {
            if (group.size() > 1) {
                duplicates += group.size() - 1;
                for (Integer idx : group) {
                    List<String> others = group.stream()
                            .filter(other -> !other.equals(idx))
                            .map(other -> contacts.get(other).suggestedName())
                            .toList();
                    dupeLabels.put(idx, others);
                }
            }
        }

        List<ParsedContact> annotated = new ArrayList<>(contacts.size());
        for (int i = 0; i < contacts.size(); i++) {
            ParsedContact c = contacts.get(i);
            annotated.add(new ParsedContact(c.originalName(), c.suggestedName(), c.phone(),
                    c.phoneE164(), c.invalidPhone(), c.alreadyExists(),
                    dupeLabels.getOrDefault(i, List.of())));
        }

        return new ImportPreview(annotated, raws.size(), invalid, duplicates, cleanupApplied, cleanupNote);
    }

    /**
     * Saves the approved rows. Re-importing is safe: an existing guest keeps their identity
     * and simply has their name and invites updated, so nobody's RSVP is orphaned.
     */
    @Transactional
    public int commit(ImportCommitRequest request) {
        Map<String, Event> eventsByKey = new HashMap<>();
        eventRepository.findAll().forEach(e -> eventsByKey.put(e.getKey(), e));

        int saved = 0;
        for (ImportCommitRequest.Row row : request.rows()) {
            Optional<String> e164 = phoneNumberService.toE164(row.phone());
            if (e164.isEmpty()) {
                continue;
            }

            Guest guest = guestRepository.findByPhoneE164(e164.get())
                    .orElseGet(() -> Guest.builder()
                            .phoneE164(e164.get())
                            .phoneDigits(phoneNumberService.toFallbackDigits(row.phone()))
                            .source(GuestSource.IMPORTED)
                            .build());

            if (row.name() != null && !row.name().isBlank()) {
                guest.setName(row.name().trim());
            }
            // Someone who self-registered and then turns up in your contacts is a real guest.
            guest.setSource(GuestSource.IMPORTED);
            guestRepository.save(guest);

            applyInvites(guest, row.eventKeys(), eventsByKey);
            saved++;
        }
        return saved;
    }

    /** Replaces the guest's invite set with exactly the keys given. */
    void applyInvites(Guest guest, List<String> eventKeys, Map<String, Event> eventsByKey) {
        List<String> wanted = eventKeys == null ? List.of() : eventKeys;
        for (Event event : eventsByKey.values()) {
            boolean shouldHave = wanted.contains(event.getKey());
            boolean has = inviteRepository.existsByGuestIdAndEventId(guest.getId(), event.getId());
            if (shouldHave && !has) {
                inviteRepository.save(GuestEventInvite.builder().guest(guest).event(event).build());
            } else if (!shouldHave && has) {
                inviteRepository.deleteByGuestIdAndEventId(guest.getId(), event.getId());
            }
        }
    }
}
