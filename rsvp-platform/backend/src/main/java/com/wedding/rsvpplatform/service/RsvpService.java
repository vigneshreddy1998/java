package com.wedding.rsvpplatform.service;

import com.wedding.rsvpplatform.dto.RsvpDto;
import com.wedding.rsvpplatform.dto.RsvpSubmitRequest;
import com.wedding.rsvpplatform.exception.NotFoundException;
import com.wedding.rsvpplatform.model.Guest;
import com.wedding.rsvpplatform.model.Rsvp;
import com.wedding.rsvpplatform.model.RsvpStatus;
import com.wedding.rsvpplatform.model.WeddingEvent;
import com.wedding.rsvpplatform.repository.GuestRepository;
import com.wedding.rsvpplatform.repository.RsvpRepository;
import com.wedding.rsvpplatform.repository.WeddingEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RsvpService {

    private final RsvpRepository rsvpRepository;
    private final GuestRepository guestRepository;
    private final WeddingEventRepository eventRepository;

    public RsvpService(RsvpRepository rsvpRepository, GuestRepository guestRepository,
                        WeddingEventRepository eventRepository) {
        this.rsvpRepository = rsvpRepository;
        this.guestRepository = guestRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public RsvpDto submit(RsvpSubmitRequest request) {
        Guest guest = guestRepository.findById(request.guestId())
                .orElseThrow(() -> new NotFoundException("Guest not found"));
        WeddingEvent event = eventRepository.findByType(request.eventType())
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (request.mealPref() != null) {
            guest.setMealPref(request.mealPref());
        }
        if (request.dietaryNotes() != null) {
            guest.setDietaryNotes(request.dietaryNotes());
        }

        Rsvp rsvp = rsvpRepository.findByGuestIdAndEventId(guest.getId(), event.getId())
                .orElseGet(() -> Rsvp.builder()
                        .guest(guest)
                        .event(event)
                        .status(RsvpStatus.PENDING)
                        .build());

        rsvp.setStatus(request.status());
        if (request.plusOneName() != null) {
            rsvp.setPlusOneName(request.plusOneName());
        }
        if (request.plusOneMealPref() != null) {
            rsvp.setPlusOneMealPref(request.plusOneMealPref());
        }

        Rsvp saved = rsvpRepository.save(rsvp);
        return new RsvpDto(saved.getEvent().getType(), saved.getStatus(),
                saved.getPlusOneName(), saved.getPlusOneMealPref());
    }
}
