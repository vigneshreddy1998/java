package com.wedding.rsvp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class GuestCreateRequest {

    @NotBlank(message = "Party name is required")
    private String partyName;

    @Min(value = 1, message = "maxGuests must be at least 1")
    private int maxGuests = 1;

    private String email;
    private String phone;

    // Optional - if left blank, the server generates one
    private String inviteCode;

    public String getPartyName() { return partyName; }
    public void setPartyName(String partyName) { this.partyName = partyName; }

    public int getMaxGuests() { return maxGuests; }
    public void setMaxGuests(int maxGuests) { this.maxGuests = maxGuests; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }
}
