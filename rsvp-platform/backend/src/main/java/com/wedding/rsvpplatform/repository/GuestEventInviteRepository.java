package com.wedding.rsvpplatform.repository;

import com.wedding.rsvpplatform.model.GuestEventInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface GuestEventInviteRepository extends JpaRepository<GuestEventInvite, UUID> {

    List<GuestEventInvite> findByGuestId(UUID guestId);

    @Query("select i.event.key from GuestEventInvite i where i.guest.id = :guestId")
    List<String> findEventKeysByGuestId(@Param("guestId") UUID guestId);

    void deleteByGuestIdAndEventId(UUID guestId, UUID eventId);

    boolean existsByGuestIdAndEventId(UUID guestId, UUID eventId);
}
