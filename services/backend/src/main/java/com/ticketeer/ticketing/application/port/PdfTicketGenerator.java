package com.ticketeer.ticketing.application.port;

import com.ticketeer.ticketing.domain.model.Ticket;

public interface PdfTicketGenerator {

    byte[] generate(Ticket ticket, byte[] qrPng);
}
