package com.wedding.rsvpplatform.service;

import com.wedding.rsvpplatform.dto.FlightDetailDto;
import com.wedding.rsvpplatform.exception.NotFoundException;
import com.wedding.rsvpplatform.model.FlightDetail;
import com.wedding.rsvpplatform.model.Guest;
import com.wedding.rsvpplatform.repository.FlightDetailRepository;
import com.wedding.rsvpplatform.repository.GuestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FlightDetailService {

    private final FlightDetailRepository flightDetailRepository;
    private final GuestRepository guestRepository;

    public FlightDetailService(FlightDetailRepository flightDetailRepository, GuestRepository guestRepository) {
        this.flightDetailRepository = flightDetailRepository;
        this.guestRepository = guestRepository;
    }

    @Transactional
    public FlightDetailDto save(UUID guestId, String flightNumber, java.time.LocalDateTime arrivalDatetime,
                                 String airport, Boolean pickupNeeded, String rawChatLog) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new NotFoundException("Guest not found"));

        FlightDetail detail = flightDetailRepository.findByGuestId(guestId)
                .orElseGet(() -> FlightDetail.builder().guest(guest).build());

        if (flightNumber != null) detail.setFlightNumber(flightNumber);
        if (arrivalDatetime != null) detail.setArrivalDatetime(arrivalDatetime);
        if (airport != null) detail.setAirport(airport);
        if (pickupNeeded != null) detail.setPickupNeeded(pickupNeeded);
        if (rawChatLog != null) detail.setRawChatLog(rawChatLog);

        FlightDetail saved = flightDetailRepository.save(detail);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<FlightDetailDto> listSortedByArrival() {
        return flightDetailRepository.findAllByOrderByArrivalDatetimeAsc().stream()
                .map(this::toDto)
                .toList();
    }

    private FlightDetailDto toDto(FlightDetail d) {
        return new FlightDetailDto(d.getGuest().getId(), d.getFlightNumber(), d.getArrivalDatetime(),
                d.getAirport(), d.getPickupNeeded());
    }
}
