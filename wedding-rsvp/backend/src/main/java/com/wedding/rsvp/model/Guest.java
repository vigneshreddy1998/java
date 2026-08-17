package com.wedding.rsvp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "guests")
public class Guest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique code shared with a household/party on their invite (e.g. "SHARMA24").
    // Only used when the site is configured to require invite codes.
    @Column(name = "invite_code", unique = true)
    private String inviteCode;

    // Name of the household/party as it should greet them, e.g. "The Sharma Family"
    @Column(name = "party_name", nullable = false)
    private String partyName;

    // How many people this invite covers (including the named guest)
    @Column(name = "max_guests", nullable = false)
    private int maxGuests = 1;

    private String email;

    private String phone;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public Guest() {}

    public Guest(String inviteCode, String partyName, int maxGuests, String email, String phone) {
        this.inviteCode = inviteCode;
        this.partyName = partyName;
        this.maxGuests = maxGuests;
        this.email = email;
        this.phone = phone;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }

    public String getPartyName() { return partyName; }
    public void setPartyName(String partyName) { this.partyName = partyName; }

    public int getMaxGuests() { return maxGuests; }
    public void setMaxGuests(int maxGuests) { this.maxGuests = maxGuests; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
