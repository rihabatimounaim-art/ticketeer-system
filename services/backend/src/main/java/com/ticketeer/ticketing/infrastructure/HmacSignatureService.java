package com.ticketeer.ticketing.infrastructure;

import com.ticketeer.ticketing.application.port.SignatureService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HmacSignatureService implements SignatureService {

    private static final String ALGORITHM = "HmacSHA256";

    private final TicketQrProperties properties;

    public HmacSignatureService(final TicketQrProperties properties) {
        this.properties = properties;
    }

    @Override
    public String sign(final String payload) {
        try {
            final Mac mac = Mac.getInstance(ALGORITHM);
            final SecretKeySpec secretKeySpec = new SecretKeySpec(
                    properties.secret().getBytes(StandardCharsets.UTF_8),
                    ALGORITHM
            );
            mac.init(secretKeySpec);

            final byte[] signatureBytes = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to sign QR payload", e);
        }
    }

    @Override
    public boolean verify(final String payload, final String signature) {
        return sign(payload).equals(signature);
    }
}
