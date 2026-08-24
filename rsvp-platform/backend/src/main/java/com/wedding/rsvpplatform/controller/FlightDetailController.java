package com.wedding.rsvpplatform.controller;

import com.wedding.rsvpplatform.dto.FlightDetailDto;
import com.wedding.rsvpplatform.dto.FlightDetailManualRequest;
import com.wedding.rsvpplatform.service.FlightDetailService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/flight-details")
public class FlightDetailController {

    private final FlightDetailService flightDetailService;

    public FlightDetailController(FlightDetailService flightDetailService) {
        this.flightDetailService = flightDetailService;
    }

    @PostMapping
    public FlightDetailDto submitManually(@Valid @RequestBody FlightDetailManualRequest request) {
        return flightDetailService.save(request.guestId(), request.flightNumber(), request.arrivalDatetime(),
                request.airport(), request.pickupNeeded(), null);
    }
}
