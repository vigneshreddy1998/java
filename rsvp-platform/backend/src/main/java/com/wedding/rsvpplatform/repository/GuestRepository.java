package com.wedding.rsvpplatform.repository;

import com.wedding.rsvpplatform.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GuestRepository extends JpaRepository<Guest, UUID> {

    Optional<Guest> findByPhoneE164(String phoneE164);

    /**
     * Fallback lookup for a number saved in a different shape than the guest typed it.
     * Returns a list because a suffix match can theoretically collide across countries —
     * callers must treat more than one hit as "not resolved" rather than picking one.
     */
    List<Guest> findByPhoneDigits(String phoneDigits);
}
