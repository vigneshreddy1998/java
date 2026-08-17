package com.wedding.rsvp.dto;

import com.wedding.rsvp.model.RsvpEntry;

import java.util.List;

public class RsvpSummary {
    private int totalResponses;
    private int totalAttendingGuests;
    private int attendingParties;
    private int decliningParties;
    private List<RsvpEntry> entries;

    public RsvpSummary(int totalResponses, int totalAttendingGuests, int attendingParties,
                        int decliningParties, List<RsvpEntry> entries) {
        this.totalResponses = totalResponses;
        this.totalAttendingGuests = totalAttendingGuests;
        this.attendingParties = attendingParties;
        this.decliningParties = decliningParties;
        this.entries = entries;
    }

    public int getTotalResponses() { return totalResponses; }
    public int getTotalAttendingGuests() { return totalAttendingGuests; }
    public int getAttendingParties() { return attendingParties; }
    public int getDecliningParties() { return decliningParties; }
    public List<RsvpEntry> getEntries() { return entries; }
}
