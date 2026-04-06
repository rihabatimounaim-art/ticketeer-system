package com.ticketeer.network.application;

import com.ticketeer.network.domain.Trip;
import com.ticketeer.network.infrastructure.SpringDataTripRepository;
import com.ticketeer.network.infrastructure.TripEntity;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

public class SearchTripsUseCase {

    private final SpringDataTripRepository tripRepository;

    public SearchTripsUseCase(final SpringDataTripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public List<Trip> execute(final String from, final String to, final LocalDate date) {
        final Instant startOfDay = date.atStartOfDay().toInstant(ZoneOffset.UTC);
        final Instant endOfDay = date.plusDays(1).atStartOfDay().minusNanos(1).toInstant(ZoneOffset.UTC);

        return tripRepository.findByDepartureStationCodeAndArrivalStationCodeAndDepartureTimeBetween(
                        from.toUpperCase(),
                        to.toUpperCase(),
                        startOfDay,
                        endOfDay
                )
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private Trip toDomain(final TripEntity entity) {
        return new Trip(
                entity.getId(),
                entity.getDepartureStationCode(),
                entity.getArrivalStationCode(),
                entity.getDepartureTime(),
                entity.getArrivalTime(),
                entity.getPrice()
        );
    }
}
