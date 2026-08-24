package com.wedding.rsvpplatform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "flight_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightDetail {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guest_id", nullable = false)
    private Guest guest;

    @Column(name = "flight_number")
    private String flightNumber;

    @Column(name = "arrival_datetime")
    private LocalDateTime arrivalDatetime;

    private String airport;

    @Column(name = "pickup_needed")
    private Boolean pickupNeeded;

    @Lob
    @Column(name = "raw_chat_log")
    private String rawChatLog;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    @PreUpdate
    void touch() {
        updatedAt = Instant.now();
    }
}
