package com.ticketeer.identity.infrastructure;

import com.ticketeer.identity.application.port.TokenGenerator;
import com.ticketeer.identity.domain.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

public class JwtTokenGenerator implements TokenGenerator {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtTokenGenerator(final JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generate(final User user) {
        final Instant now = Instant.now();
        final Instant exp = now.plusSeconds(jwtProperties.expirationSeconds());

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("role", user.getRole().name())
                .claim("email", user.getEmail())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(secretKey)
                .compact();
    }
}
