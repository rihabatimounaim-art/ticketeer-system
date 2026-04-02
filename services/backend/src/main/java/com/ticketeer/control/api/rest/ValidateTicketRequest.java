package com.ticketeer.control.api.rest;

public record ValidateTicketRequest(
        String ticketId,
        String agentId
) {
}
