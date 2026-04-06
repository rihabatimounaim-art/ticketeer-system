package com.ticketeer.network.api;

import com.ticketeer.network.application.SearchTripsUseCase;
import com.ticketeer.network.domain.Trip;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/trips")
public class TripController {

    private final SearchTripsUseCase searchTripsUseCase;

    public TripController(final SearchTripsUseCase searchTripsUseCase) {
        this.searchTripsUseCase = searchTripsUseCase;
    }

    @GetMapping("/search")
    public List<TripResponse> search(@RequestParam final String from,
                                     @RequestParam final String to,
                                     @RequestParam final String date) {
        final List<Trip> trips = searchTripsUseCase.execute(from, to, LocalDate.parse(date));

        return trips.stream()
                .map(trip -> new TripResponse(
                        trip.getId().toString(),
                        trip.getDepartureStationCode(),
                        trip.getArrivalStationCode(),
                        trip.getDepartureTime().toString(),
                        trip.getArrivalTime().toString(),
                        trip.getPrice()
                ))
                .toList();
    }
}
