package com.ticketeer.identity.api.rest;

/**
 * Transport request payload for login.
 */
public record LoginRequest(
        String email,
        String password
) {
}
