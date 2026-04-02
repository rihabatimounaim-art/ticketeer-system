package com.ticketeer.ticketing.infrastructure;

import com.ticketeer.ticketing.application.port.TicketRepository;
import com.ticketeer.ticketing.domain.model.Ticket;
import com.ticketeer.ticketing.domain.model.TicketId;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryTicketRepository implements TicketRepository {

    private final Map<TicketId, Ticket> storage = new HashMap<>();

    @Override
    public Ticket save(Ticket ticket) {
        storage.put(ticket.getId(), ticket);
        return ticket;
    }

    @Override
    public Optional<Ticket> findById(TicketId ticketId) {
        return Optional.ofNullable(storage.get(ticketId));
    }
}
