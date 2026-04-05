package com.ticketeer.ticketing.infrastructure;

import com.ticketeer.identity.domain.model.UserId;
import com.ticketeer.ticketing.application.port.TicketRepository;
import com.ticketeer.ticketing.domain.model.Ticket;
import com.ticketeer.ticketing.domain.model.TicketId;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryTicketRepository implements TicketRepository {

    private final Map<TicketId, Ticket> storage = new HashMap<>();

    @Override
    public Ticket save(final Ticket ticket) {
        storage.put(ticket.getId(), ticket);
        return ticket;
    }

    @Override
    public Optional<Ticket> findById(final TicketId ticketId) {
        return Optional.ofNullable(storage.get(ticketId));
    }

    @Override
    public List<Ticket> findByHolderId(final UserId holderId) {
        return storage.values()
                .stream()
                .filter(ticket -> ticket.getHolderId().equals(holderId))
                .toList();
    }
}
