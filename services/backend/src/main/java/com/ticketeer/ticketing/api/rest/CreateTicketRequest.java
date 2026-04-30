package com.ticketeer.ticketing.api.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateTicketRequest(
        @NotBlank(message = "Holder ID is required")
        String holderId,

        @NotBlank(message = "Valid from date is required")
        String validFrom,

        @NotBlank(message = "Valid until date is required")
        String validUntil,

        @NotBlank(message = "Departure station code is required")
        String departureStationCode,

        @NotBlank(message = "Arrival station code is required")
        String arrivalStationCode,

        @NotBlank(message = "Departure time is required")
        String departureTime,

        @NotBlank(message = "Arrival time is required")
        String arrivalTime,

        @Positive(message = "Price must be positive")
        double price
) {
}
