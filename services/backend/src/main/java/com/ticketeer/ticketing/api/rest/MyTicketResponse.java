package com.ticketeer.ticketing.api.rest;

public record MyTicketResponse(
        String id,
        String holderId,
        String departureStationCode,
        String arrivalStationCode,
        String departureTime,
        String arrivalTime,
        double price,
        String validFrom,
        String validUntil,
        String status,
        String issuedAt
) {
}
