package com.wedding.rsvpplatform.controller;

import com.wedding.rsvpplatform.dto.EventDto;
import com.wedding.rsvpplatform.exception.NotFoundException;
import com.wedding.rsvpplatform.model.EventType;
import com.wedding.rsvpplatform.repository.WeddingEventRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final WeddingEventRepository eventRepository;

    public EventController(WeddingEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping
    public List<EventDto> listEvents() {
        return eventRepository.findAll().stream()
                .map(e -> new EventDto(e.getType(), e.getName(), e.getDate(), e.getVenue(), e.getDressCode()))
                .toList();
    }

    @GetMapping("/{type}")
    public EventDto getEvent(@PathVariable EventType type) {
        return eventRepository.findByType(type)
                .map(e -> new EventDto(e.getType(), e.getName(), e.getDate(), e.getVenue(), e.getDressCode()))
                .orElseThrow(() -> new NotFoundException("Event not found"));
    }
}
