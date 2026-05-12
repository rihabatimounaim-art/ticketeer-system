package com.ticketeer.control.api.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ValidateTicketRequest(
        @NotNull
        UUID ticketId,

        @NotBlank
        String departureStationCode,

        @NotBlank
        String arrivalStationCode
) {
}
