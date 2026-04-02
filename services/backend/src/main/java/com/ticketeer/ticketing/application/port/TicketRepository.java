package com.ticketeer.ticketing.application.port;

import com.ticketeer.ticketing.domain.model.Ticket;
import com.ticketeer.ticketing.domain.model.TicketId;

import java.util.Optional;

/**
 * Port for ticket persistence.
 */
public interface TicketRepository {

    Ticket save(Ticket ticket);

    Optional<Ticket> findById(TicketId ticketId);
}
