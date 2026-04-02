package com.ticketeer.identity.api.rest;

/**
 * Transport response payload for login.
 */
public record LoginResponse(
        String token
) {
}
