package com.ticketeer.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

public class JwtParser {

    public JwtClaims parse(final String token) {
        final String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new RuntimeException("Invalid JWT");
        }

        final String payloadJson = new String(
                Base64.getUrlDecoder().decode(parts[1]),
                StandardCharsets.UTF_8
        );

        final String subject = extractString(payloadJson, "sub");
        final String role = extractString(payloadJson, "role");
        final String email = extractString(payloadJson, "email");
        final long iat = extractLong(payloadJson, "iat");
        final long exp = extractLong(payloadJson, "exp");

        if (Instant.now().getEpochSecond() > exp) {
            throw new RuntimeException("JWT expired");
        }

        return new JwtClaims(subject, role, email, iat, exp);
    }

    private String extractString(final String json, final String field) {
        final String pattern = "\"" + field + "\":\"";
        int start = json.indexOf(pattern);
        if (start == -1) {
            throw new RuntimeException("Missing field: " + field);
        }
        start += pattern.length();
        int end = json.indexOf("\"", start);
        if (end == -1) {
            throw new RuntimeException("Invalid string field: " + field);
        }
        return json.substring(start, end);
    }

    private long extractLong(final String json, final String field) {
        final String pattern = "\"" + field + "\":";
        int start = json.indexOf(pattern);
        if (start == -1) {
            throw new RuntimeException("Missing field: " + field);
        }
        start += pattern.length();

        int end = start;
        while (end < json.length() && Character.isDigit(json.charAt(end))) {
            end++;
        }

        if (start == end) {
            throw new RuntimeException("Invalid numeric field: " + field);
        }

        return Long.parseLong(json.substring(start, end));
    }
}
