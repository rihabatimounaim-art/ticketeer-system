package com.ticketeer.shared.api;

public record ErrorResponse(
        String code,
        String message,
        String timestamp
) {
}
