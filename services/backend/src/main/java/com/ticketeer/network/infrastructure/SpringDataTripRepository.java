package com.ticketeer.network.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface SpringDataTripRepository extends JpaRepository<TripEntity, UUID> {

    List<TripEntity> findByDepartureStationCodeAndArrivalStationCodeAndDepartureTimeBetween(
            String departureStationCode,
            String arrivalStationCode,
            Instant startOfDay,
            Instant endOfDay
    );
}
