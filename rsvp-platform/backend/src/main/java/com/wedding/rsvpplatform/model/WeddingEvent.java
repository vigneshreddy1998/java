package com.wedding.rsvpplatform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "events", uniqueConstraints = @UniqueConstraint(columnNames = "type"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeddingEvent {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType type;

    @Column(nullable = false)
    private String name;

    @Column(name = "event_date")
    private LocalDateTime date;

    private String venue;

    @Column(name = "dress_code")
    private String dressCode;
}
