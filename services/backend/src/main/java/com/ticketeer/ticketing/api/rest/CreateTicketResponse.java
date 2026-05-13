package com.ticketeer.ticketing.api.rest;

public record CreateTicketResponse(
        String id,
        String status,
        double finalPrice,
        double originalPrice,
        int discountPercent,
        String discountLabel
) {
}
