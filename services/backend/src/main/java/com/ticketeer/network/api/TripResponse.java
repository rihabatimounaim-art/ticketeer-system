package com.ticketeer.network.api;

public record TripResponse(
        String id,
        String departureStationCode,
        String arrivalStationCode,
        String departureTime,
        String arrivalTime,
        double price,
        double discountedPrice,
        int discountPercent,
        String discountLabel
) {
}
