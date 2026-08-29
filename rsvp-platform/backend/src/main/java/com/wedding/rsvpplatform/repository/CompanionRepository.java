package com.wedding.rsvpplatform.repository;

import com.wedding.rsvpplatform.model.Companion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CompanionRepository extends JpaRepository<Companion, UUID> {

    List<Companion> findByGuestIdAndEventId(UUID guestId, UUID eventId);

    /** Bulk delete so the replace-then-insert ordering is deterministic — see SongPickRepository. */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Companion c where c.guest.id = :guestId and c.event.id = :eventId")
    void deleteAllForGuestAndEvent(@Param("guestId") UUID guestId, @Param("eventId") UUID eventId);
}
