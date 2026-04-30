package com.ticketeer.control.api.rest;

import jakarta.validation.constraints.NotBlank;

public record ValidateTicketRequest(
        @NotBlank(message = "Ticket ID is required")
        String ticketId
) {
}
