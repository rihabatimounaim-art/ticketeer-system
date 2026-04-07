package com.ticketeer.ticketing.api.rest;

public record CreateTicketRequest(
        String holderId,
        String validFrom,
        String validUntil,
        String departureStationCode,
        String arrivalStationCode,
        String departureTime,
        String arrivalTime,
        double price
) {
}
