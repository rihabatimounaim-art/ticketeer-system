package com.ticketeer.identity.infrastructure;

import com.ticketeer.identity.application.port.TokenGenerator;
import com.ticketeer.identity.domain.model.User;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

public class JwtTokenGenerator implements TokenGenerator {

    private final JwtProperties jwtProperties;

    public JwtTokenGenerator(final JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public String generate(final User user) {
        try {
            final String headerJson = """
                    {\"alg\":\"HS256\",\"typ\":\"JWT\"}
                    """.trim();

            final long now = Instant.now().getEpochSecond();
            final long exp = now + jwtProperties.expirationSeconds();

            final String payloadJson = """
                    {\"sub\":\"%s\",\"role\":\"%s\",\"email\":\"%s\",\"iat\":%d,\"exp\":%d}
                    """.formatted(
                    user.getId().toString(),
                    user.getRole().name(),
                    user.getEmail(),
                    now,
                    exp
            );

            final String header = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));
            final String payload = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));
            final String unsignedToken = header + "." + payload;
            final String signature = sign(unsignedToken, jwtProperties.secret());

            return unsignedToken + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate JWT", e);
        }
    }

    private String sign(final String content, final String secret) throws Exception {
        final Mac mac = Mac.getInstance("HmacSHA256");
        final SecretKeySpec secretKeySpec =
                new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        return base64UrlEncode(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
    }

    private String base64UrlEncode(final byte[] content) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(content);
    }
}
