package com.wedding.rsvp.repository;

import com.wedding.rsvp.model.RsvpEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RsvpRepository extends JpaRepository<RsvpEntry, Long> {
    Optional<RsvpEntry> findByEmailIgnoreCaseAndEventType(String email, String eventType);
    Optional<RsvpEntry> findByGuestNameIgnoreCaseAndEventTypeAndEmailIsNull(String guestName, String eventType);
    List<RsvpEntry> findAllByOrderBySubmittedAtDesc();
}
