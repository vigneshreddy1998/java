package com.wedding.rsvp.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "rsvp_entries")
public class RsvpEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which event this response is for, e.g. "WEDDING" or "SANGEETH".
    // Lets one guest list cover multiple pre-wedding events.
    @Column(name = "event_type", nullable = false)
    private String eventType = "WEDDING";

    // Set only when the guest came through an invite-code lookup
    @Column(name = "guest_id")
    private Long guestId;

    @Column(name = "guest_name", nullable = false)
    private String guestName;

    // Optional — some RSVP flows (e.g. the Sangeeth invite) don't collect email
    private String email;

    @Column(nullable = false)
    private boolean attending;

    @Column(name = "guest_count", nullable = false)
    private int guestCount = 1;

    // Comma-separated meal choice per attending guest, e.g. "Chicken, Vegetarian"
    @Column(name = "meal_selections", columnDefinition = "TEXT")
    private String mealSelections;

    @Column(columnDefinition = "TEXT")
    private String allergies;

    @Column(name = "song_request", columnDefinition = "TEXT")
    private String songRequest;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt = Instant.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public Long getGuestId() { return guestId; }
    public void setGuestId(Long guestId) { this.guestId = guestId; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isAttending() { return attending; }
    public void setAttending(boolean attending) { this.attending = attending; }

    public int getGuestCount() { return guestCount; }
    public void setGuestCount(int guestCount) { this.guestCount = guestCount; }

    public String getMealSelections() { return mealSelections; }
    public void setMealSelections(String mealSelections) { this.mealSelections = mealSelections; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    public String getSongRequest() { return songRequest; }
    public void setSongRequest(String songRequest) { this.songRequest = songRequest; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Instant getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Instant submittedAt) { this.submittedAt = submittedAt; }
}
