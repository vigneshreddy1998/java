package com.wedding.rsvpplatform.repository;

import com.wedding.rsvpplatform.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
    Optional<Event> findByKey(String key);
    List<Event> findAllByOrderByDisplayOrderAsc();
}
