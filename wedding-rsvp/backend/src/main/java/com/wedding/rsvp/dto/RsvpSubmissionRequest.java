package com.wedding.rsvp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class RsvpSubmissionRequest {

    // Which event this response is for, e.g. "WEDDING" or "SANGEETH".
    // Defaults to "WEDDING" server-side when left blank.
    private String eventType;

    // Present only when the guest looked themselves up via invite code
    private Long guestId;

    @NotBlank(message = "Name is required")
    private String guestName;

    // Optional — flows like the Sangeeth invite don't collect an email
    @Email(message = "Enter a valid email")
    private String email;

    @NotNull(message = "Let us know if you're attending")
    private Boolean attending;

    @Min(value = 1, message = "Guest count must be at least 1")
    private int guestCount = 1;

    private List<String> mealSelections;

    private String allergies;

    private String songRequest;

    private String message;

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public Long getGuestId() { return guestId; }
    public void setGuestId(Long guestId) { this.guestId = guestId; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getAttending() { return attending; }
    public void setAttending(Boolean attending) { this.attending = attending; }

    public int getGuestCount() { return guestCount; }
    public void setGuestCount(int guestCount) { this.guestCount = guestCount; }

    public List<String> getMealSelections() { return mealSelections; }
    public void setMealSelections(List<String> mealSelections) { this.mealSelections = mealSelections; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    public String getSongRequest() { return songRequest; }
    public void setSongRequest(String songRequest) { this.songRequest = songRequest; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
