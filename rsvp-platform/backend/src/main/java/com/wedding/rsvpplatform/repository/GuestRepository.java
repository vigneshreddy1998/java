package com.wedding.rsvpplatform.repository;

import com.wedding.rsvpplatform.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GuestRepository extends JpaRepository<Guest, UUID> {
    List<Guest> findByFamilyId(UUID familyId);
}
