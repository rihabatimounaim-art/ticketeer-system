package com.ticketeer.ticketing.application.port;

import com.ticketeer.ticketing.domain.model.Ticket;

/**
 * Port responsible for generating QR codes from tickets.
 */
public interface QrCodeGenerator {

    String generate(Ticket ticket);
}
