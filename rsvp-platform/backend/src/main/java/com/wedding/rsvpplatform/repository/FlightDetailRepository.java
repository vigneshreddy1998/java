package com.wedding.rsvpplatform.repository;

import com.wedding.rsvpplatform.model.FlightDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FlightDetailRepository extends JpaRepository<FlightDetail, UUID> {
    Optional<FlightDetail> findByGuestId(UUID guestId);
    List<FlightDetail> findAllByOrderByArrivalDatetimeAsc();
}
