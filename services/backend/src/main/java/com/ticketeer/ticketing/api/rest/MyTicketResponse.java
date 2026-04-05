package com.ticketeer.ticketing.api.rest;

public record MyTicketResponse(
        String ticketId,
        String holderId,
        String validFrom,
        String validUntil,
        String status,
        String issuedAt
) {
}
