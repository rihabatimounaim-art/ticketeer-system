package com.ticketeer.security;

public record JwtAuthenticatedUser(
        String userId,
        String email,
        String role
) {
}
