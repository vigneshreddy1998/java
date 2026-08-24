package com.wedding.rsvpplatform.repository;

import com.wedding.rsvpplatform.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface SongRepository extends JpaRepository<Song, UUID> {

    /**
     * Atomic claim: the WHERE clause only matches an unclaimed row, so under concurrent
     * requests only one transaction's UPDATE affects a row (Postgres row-level locking on
     * the UPDATE itself) — the loser gets 0 affected rows back instead of overwriting the winner.
     */
    @Modifying
    @Query(value = "update songs set claimed_by_family_id = :familyId, locked = true, version = version + 1 " +
                   "where id = :songId and claimed_by_family_id is null",
           nativeQuery = true)
    int claimIfUnclaimed(@Param("songId") UUID songId, @Param("familyId") UUID familyId);
}
