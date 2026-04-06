package com.ticketeer.network.api;

public record TripResponse(
        String tripId,
        String from,
        String to,
        String departureTime,
        String arrivalTime,
        double price
) {
}
