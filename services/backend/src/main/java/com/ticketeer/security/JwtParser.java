package com.ticketeer.security;

import com.ticketeer.identity.infrastructure.JwtProperties;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

public class JwtParser {

    private final JwtProperties jwtProperties;

    public JwtParser(final JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public JwtClaims parse(final String token) {
        final String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new RuntimeException("Invalid JWT");
        }

        final String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);

        final String subject = extract(payloadJson, "sub");
        final String role = extract(payloadJson, "role");
        final String email = extract(payloadJson, "email");
        final long iat = Long.parseLong(extract(payloadJson, "iat"));
        final long exp = Long.parseLong(extract(payloadJson, "exp"));

        if (Instant.now().getEpochSecond() > exp) {
            throw new RuntimeException("JWT expired");
        }

        return new JwtClaims(subject, role, email, iat, exp);
    }

    private String extract(String json, String field) {
        final String pattern = "\"" + field + "\":\"";
        int start = json.indexOf(pattern);
        if (start == -1) return null;
        start += pattern.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}
