package com.wedding.rsvp.repository;

import com.wedding.rsvp.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuestRepository extends JpaRepository<Guest, Long> {
    Optional<Guest> findByInviteCodeIgnoreCase(String inviteCode);
}
