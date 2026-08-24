package com.wedding.rsvpplatform.repository;

import com.wedding.rsvpplatform.model.EventType;
import com.wedding.rsvpplatform.model.WeddingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WeddingEventRepository extends JpaRepository<WeddingEvent, UUID> {
    Optional<WeddingEvent> findByType(EventType type);
}
