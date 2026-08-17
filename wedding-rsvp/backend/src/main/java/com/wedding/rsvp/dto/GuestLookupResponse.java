package com.wedding.rsvp.dto;

public class GuestLookupResponse {
    private Long guestId;
    private String partyName;
    private int maxGuests;
    private String email;

    public GuestLookupResponse(Long guestId, String partyName, int maxGuests, String email) {
        this.guestId = guestId;
        this.partyName = partyName;
        this.maxGuests = maxGuests;
        this.email = email;
    }

    public Long getGuestId() { return guestId; }
    public String getPartyName() { return partyName; }
    public int getMaxGuests() { return maxGuests; }
    public String getEmail() { return email; }
}
