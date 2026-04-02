package com.ticketeer.security;

import com.ticketeer.identity.infrastructure.JwtProperties;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class JwtTokenValidator {

    private final JwtProperties jwtProperties;

    public JwtTokenValidator(final JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public boolean isValid(final String token) {
        try {
            final String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }

            final String unsignedToken = parts[0] + "." + parts[1];
            final String expectedSignature = sign(unsignedToken, jwtProperties.secret());

            return expectedSignature.equals(parts[2]);
        } catch (Exception e) {
            return false;
        }
    }

    private String sign(final String content, final String secret) throws Exception {
        final Mac mac = Mac.getInstance("HmacSHA256");
        final SecretKeySpec secretKeySpec =
                new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
    }
}
