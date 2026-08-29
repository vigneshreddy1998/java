package com.wedding.rsvpplatform.service;

import com.wedding.rsvpplatform.dto.VerifyResponse;
import com.wedding.rsvpplatform.exception.RateLimitedException;
import com.wedding.rsvpplatform.exception.UnauthorizedException;
import com.wedding.rsvpplatform.model.Event;
import com.wedding.rsvpplatform.model.Guest;
import com.wedding.rsvpplatform.model.GuestEventInvite;
import com.wedding.rsvpplatform.model.GuestSource;
import com.wedding.rsvpplatform.repository.EventRepository;
import com.wedding.rsvpplatform.repository.GuestEventInviteRepository;
import com.wedding.rsvpplatform.repository.GuestRepository;
import com.wedding.rsvpplatform.security.JwtService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Turns a phone number into a session. This is the entire front door.
 *
 * <p>Two paths produce an identical-looking result, on purpose: a number already on the guest
 * list resolves to that guest's invites, and a number that isn't creates a self-registered
 * guest with access to the open events only. A visitor cannot tell which happened, so nobody
 * can probe whether a given number was invited — or learn that a private event exists.
 */
@Service
public class GuestAccessService {

    private final GuestRepository guestRepository;
    private final EventRepository eventRepository;
    private final GuestEventInviteRepository inviteRepository;
    private final PhoneNumberService phoneNumberService;
    private final RateLimiter rateLimiter;
    private final JwtService jwtService;

    public GuestAccessService(GuestRepository guestRepository,
                               EventRepository eventRepository,
                               GuestEventInviteRepository inviteRepository,
                               PhoneNumberService phoneNumberService,
                               RateLimiter rateLimiter,
                               JwtService jwtService) {
        this.guestRepository = guestRepository;
        this.eventRepository = eventRepository;
        this.inviteRepository = inviteRepository;
        this.phoneNumberService = phoneNumberService;
        this.rateLimiter = rateLimiter;
        this.jwtService = jwtService;
    }

    @Transactional
    public VerifyResponse verify(String rawPhone, String providedName, String clientKey) {
        if (rateLimiter.isBlocked(clientKey)) {
            throw new RateLimitedException("Too many attempts. Please wait a few minutes and try again.");
        }

        Optional<String> e164 = phoneNumberService.toE164(rawPhone);
        if (e164.isEmpty()) {
            // Unparseable input is the one case we reject outright — there's no number to
            // create a guest for. Counts against the limiter so this isn't a free probe.
            rateLimiter.recordFailure(clientKey);
            throw new UnauthorizedException("That doesn't look like a valid phone number. Please check and try again.");
        }

        rateLimiter.recordSuccess(clientKey);

        Guest guest = findExisting(e164.get(), rawPhone)
                .orElseGet(() -> createSelfRegistered(e164.get(), rawPhone, providedName));

        if (guest.getName() == null && providedName != null && !providedName.isBlank()) {
            guest.setName(providedName.trim());
            guestRepository.save(guest);
        }

        List<String> eventKeys = visibleEventKeys(guest);
        String token = jwtService.generateGuestToken(guest.getId(), eventKeys);

        return new VerifyResponse(token, guest.getName(), guest.getName() == null);
    }

    /**
     * Exact E.164 first, then the trailing-digits fallback for a number saved in a different
     * shape. The fallback only counts when it identifies exactly one guest — an ambiguous
     * suffix match is treated as no match rather than guessing between two people.
     */
    private Optional<Guest> findExisting(String e164, String rawPhone) {
        Optional<Guest> exact = guestRepository.findByPhoneE164(e164);
        if (exact.isPresent()) {
            return exact;
        }
        List<Guest> bySuffix = guestRepository.findByPhoneDigits(phoneNumberService.toFallbackDigits(rawPhone));
        return bySuffix.size() == 1 ? Optional.of(bySuffix.get(0)) : Optional.empty();
    }

    private Guest createSelfRegistered(String e164, String rawPhone, String providedName) {
        Guest guest = guestRepository.save(Guest.builder()
                .phoneE164(e164)
                .phoneDigits(phoneNumberService.toFallbackDigits(rawPhone))
                .name(providedName != null && !providedName.isBlank() ? providedName.trim() : null)
                .source(GuestSource.SELF_REGISTERED)
                .build());

        // Self-registered guests get every event that isn't invite-gated. They never gain
        // access to a private event by signing themselves up.
        eventRepository.findAll().stream()
                .filter(e -> !e.isRequiresInvite())
                .forEach(e -> inviteRepository.save(
                        GuestEventInvite.builder().guest(guest).event(e).build()));

        return guest;
    }

    /**
     * Open events are visible to everyone verified; gated events only via an explicit invite.
     * This list is what gets signed into the token and enforced on every later request.
     */
    private List<String> visibleEventKeys(Guest guest) {
        List<String> invited = inviteRepository.findEventKeysByGuestId(guest.getId());
        List<String> keys = new ArrayList<>();
        for (Event event : eventRepository.findAllByOrderByDisplayOrderAsc()) {
            if (!event.isRequiresInvite() || invited.contains(event.getKey())) {
                keys.add(event.getKey());
            }
        }
        return keys;
    }
}
