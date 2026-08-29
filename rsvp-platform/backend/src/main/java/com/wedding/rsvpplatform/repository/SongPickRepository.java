package com.wedding.rsvpplatform.repository;

import com.wedding.rsvpplatform.model.SongPick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SongPickRepository extends JpaRepository<SongPick, UUID> {

    List<SongPick> findByGuestId(UUID guestId);

    /**
     * Bulk delete rather than a derived {@code deleteBy...}, because the picks are replaced
     * wholesale: a derived delete only queues entity removals, and Hibernate flushes INSERTs
     * before DELETEs. Re-inserting a song the guest already had then collided with the
     * (guest_id, song_id) unique constraint and failed the whole submission — so a guest could
     * never change their selection while keeping any song. A bulk delete executes immediately,
     * and clearing the context afterwards stops the removed rows being reused.
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from SongPick p where p.guest.id = :guestId")
    void deleteAllForGuest(@Param("guestId") UUID guestId);

    @Query("select p from SongPick p join fetch p.guest join fetch p.song")
    List<SongPick> findAllWithGuestAndSong();
}
