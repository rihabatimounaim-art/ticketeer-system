package com.ticketeer.ticketing.infrastructure;

import com.ticketeer.ticketing.application.port.QrCodeGenerator;
import com.ticketeer.ticketing.domain.model.Ticket;
import org.springframework.stereotype.Component;

@Component
public class FakeQrCodeGenerator implements QrCodeGenerator {

    @Override
    public String generate(final Ticket ticket) {
        return "QR-" + ticket.getId().toString();
    }
}
