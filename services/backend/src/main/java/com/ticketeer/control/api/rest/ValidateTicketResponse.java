package com.ticketeer.control.api.rest;

import java.util.UUID;

public record ValidateTicketResponse(
        UUID ticketId,
        String result,
        String reason,
        String message
) {
}