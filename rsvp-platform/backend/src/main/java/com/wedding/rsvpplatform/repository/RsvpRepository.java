package com.wedding.rsvpplatform.repository;

import com.wedding.rsvpplatform.model.EventType;
import com.wedding.rsvpplatform.model.Rsvp;
import com.wedding.rsvpplatform.model.RsvpStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RsvpRepository extends JpaRepository<Rsvp, UUID> {

    Optional<Rsvp> findByGuestIdAndEventId(UUID guestId, UUID eventId);

    @Query("select r from Rsvp r where r.event.type = :eventType")
    List<Rsvp> findByEventType(@Param("eventType") EventType eventType);

    @Query("select r from Rsvp r where r.event.type = :eventType and r.status = :status")
    List<Rsvp> findByEventTypeAndStatus(@Param("eventType") EventType eventType,
                                         @Param("status") RsvpStatus status);

    @Query("select r from Rsvp r where r.guest.family.id = :familyId")
    List<Rsvp> findByFamilyId(@Param("familyId") UUID familyId);

    List<Rsvp> findByStatus(RsvpStatus status);
}
