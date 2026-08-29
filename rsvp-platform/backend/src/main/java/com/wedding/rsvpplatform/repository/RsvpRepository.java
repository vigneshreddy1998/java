package com.wedding.rsvpplatform.repository;

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

    List<Rsvp> findByGuestId(UUID guestId);

    @Query("select r from Rsvp r join fetch r.guest join fetch r.event where r.event.key = :eventKey")
    List<Rsvp> findByEventKey(@Param("eventKey") String eventKey);

    @Query("select r from Rsvp r join fetch r.guest join fetch r.event")
    List<Rsvp> findAllWithGuestAndEvent();

    long countByEventIdAndStatus(UUID eventId, RsvpStatus status);

    @Query("select coalesce(sum(r.headcount), 0) from Rsvp r where r.event.id = :eventId and r.status = :status")
    long sumHeadcount(@Param("eventId") UUID eventId, @Param("status") RsvpStatus status);
}
