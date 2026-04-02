package com.ticketeer.identity.infrastructure;

public record JwtProperties(
        String secret,
        long expirationSeconds
) {
}
