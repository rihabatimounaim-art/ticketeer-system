package com.ticketeer.ticketing.infrastructure;

import com.ticketeer.ticketing.application.port.QrCodeGenerator;
import org.springframework.stereotype.Component;

@Component
public class FakeQrCodeGenerator implements QrCodeGenerator {

    @Override
    public String generate(final String payload, final String signature) {
        return payload + "|sig=" + signature;
    }
}
