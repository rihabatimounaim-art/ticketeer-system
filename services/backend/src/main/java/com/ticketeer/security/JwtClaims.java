package com.ticketeer.security;

public record JwtClaims(
        String subject,
        String role,
        String email,
        long issuedAt,
        long expiresAt
) {
}
