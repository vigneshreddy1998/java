package com.wedding.rsvpplatform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

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
    private WeddingEvent event;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RsvpStatus status = RsvpStatus.PENDING;

    @Column(name = "plus_one_name")
    private String plusOneName;

    @Enumerated(EnumType.STRING)
    @Column(name = "plus_one_meal_pref")
    private MealPref plusOneMealPref;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    @PreUpdate
    void touch() {
        updatedAt = Instant.now();
    }
}
