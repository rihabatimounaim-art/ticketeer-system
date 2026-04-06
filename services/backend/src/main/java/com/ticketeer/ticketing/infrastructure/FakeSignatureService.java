package com.ticketeer.ticketing.infrastructure;

import com.ticketeer.ticketing.application.port.SignatureService;
import org.springframework.stereotype.Component;

@Component
public class FakeSignatureService implements SignatureService {

    @Override
    public String sign(final String payload) {
        return "SIGN-" + payload.hashCode();
    }

    @Override
    public boolean verify(final String payload, final String signature) {
        return sign(payload).equals(signature);
    }
}
