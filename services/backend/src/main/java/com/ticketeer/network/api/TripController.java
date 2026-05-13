package com.ticketeer.network.api;

import com.ticketeer.network.application.SearchTripsUseCase;
import com.ticketeer.network.domain.Trip;
import com.ticketeer.shared.domain.time.DomainClock;
import com.ticketeer.ticketing.domain.service.DiscountResult;
import com.ticketeer.ticketing.domain.service.SeasonDiscountPolicy;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Trips", description = "Search available train trips")
@RestController
@RequestMapping("/trips")
public class TripController {

    private final SearchTripsUseCase searchTripsUseCase;
    private final SeasonDiscountPolicy discountPolicy;
    private final DomainClock clock;

    public TripController(final SearchTripsUseCase searchTripsUseCase,
                          final SeasonDiscountPolicy discountPolicy,
                          final DomainClock clock) {
        this.searchTripsUseCase = searchTripsUseCase;
        this.discountPolicy = discountPolicy;
        this.clock = clock;
    }

    @Operation(summary = "Search trips", description = "Search trips by origin, destination and date (YYYY-MM-DD). Returns discounted prices when applicable.")
    @GetMapping("/search")
    public List<TripResponse> search(@RequestParam final String from,
                                     @RequestParam final String to,
                                     @RequestParam final String date) {
        final var now = clock.now();
        final List<Trip> trips = searchTripsUseCase.execute(from, to, LocalDate.parse(date));
        return trips.stream()
                .map(trip -> {
                    final DiscountResult discount = discountPolicy.apply(trip.getPrice(), trip.getDepartureTime(), now);
                    return new TripResponse(
                            trip.getId().toString(),
                            trip.getDepartureStationCode(),
                            trip.getArrivalStationCode(),
                            trip.getDepartureTime().toString(),
                            trip.getArrivalTime().toString(),
                            discount.originalPrice(),
                            discount.finalPrice(),
                            discount.discountPercent(),
                            discount.discountLabel()
                    );
                })
                .toList();
    }
}
