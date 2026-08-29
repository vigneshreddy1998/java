package com.wedding.rsvpplatform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * One row per guest per event. The events are answered independently — someone may come to
 * the wedding and skip the Sangeet, and the two are catered separately.
 */
@Entity
@Table(name = "rsvps", uniqueConstraints = @UniqueConstraint(columnNames = {"guest_id", "event_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rsvp {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RsvpStatus status = RsvpStatus.PENDING;

    /** Total attending including the guest themselves. */
    @Column(nullable = false)
    @Builder.Default
    private int headcount = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_pref")
    private MealPref mealPref;

    @Column(name = "dietary_notes")
    private String dietaryNotes;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    @PreUpdate
    void touch() {
        updatedAt = Instant.now();
    }
}
