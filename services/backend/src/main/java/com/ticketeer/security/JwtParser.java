package com.ticketeer.security;

import com.ticketeer.identity.infrastructure.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class JwtParser {

    private final SecretKey secretKey;

    public JwtParser(final JwtProperties jwtProperties) {
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public JwtClaims parse(final String token) {
        final Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return new JwtClaims(
                claims.getSubject(),
                claims.get("role", String.class),
                claims.get("email", String.class),
                claims.getIssuedAt().getTime() / 1000L,
                claims.getExpiration().getTime() / 1000L
        );
    }
}
