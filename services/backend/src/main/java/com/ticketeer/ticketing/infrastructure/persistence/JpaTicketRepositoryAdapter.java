package com.ticketeer.ticketing.infrastructure.persistence;

import com.ticketeer.identity.domain.model.UserId;
import com.ticketeer.shared.domain.model.DateRange;
import com.ticketeer.ticketing.application.port.TicketRepository;
import com.ticketeer.ticketing.domain.model.Ticket;
import com.ticketeer.ticketing.domain.model.TicketId;
import com.ticketeer.ticketing.domain.model.TicketStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class JpaTicketRepositoryAdapter implements TicketRepository {

    private final SpringDataTicketRepository repository;

    public JpaTicketRepositoryAdapter(SpringDataTicketRepository repository) {
        this.repository = repository;
    }

    @Override
    public Ticket save(Ticket ticket) {
        TicketEntity entity = new TicketEntity(
                UUID.fromString(ticket.getId().toString()),
                UUID.fromString(ticket.getHolderId().toString()),
                ticketValidityStart(ticket),
                ticketValidityEnd(ticket),
                ticket.getStatus().name(),
                ticketIssuedAt(ticket)
        );

        TicketEntity saved = repository.save(entity);

        return toDomain(saved);
    }

    @Override
    public Optional<Ticket> findById(TicketId ticketId) {
        return repository.findById(UUID.fromString(ticketId.toString()))
                .map(this::toDomain);
    }

    private Ticket toDomain(TicketEntity entity) {
        Ticket ticket = new Ticket(
                new TicketId(entity.getId().toString()),
                new UserId(entity.getHolderId().toString()),
                new DateRange(entity.getValidFrom(), entity.getValidUntil()),
                entity.getIssuedAt()
        );

        if (TicketStatus.valueOf(entity.getStatus()) == TicketStatus.VALID) {
            ticket.activate();
        }

        return ticket;
    }

    private java.time.Instant ticketValidityStart(Ticket ticket) {
        return ticket.getClass() != null ? ticketValidityWindow(ticket).start() : null;
    }

    private java.time.Instant ticketValidityEnd(Ticket ticket) {
        return ticket.getClass() != null ? ticketValidityWindow(ticket).end() : null;
    }

    private DateRange ticketValidityWindow(Ticket ticket) {
        try {
            var field = Ticket.class.getDeclaredField("validityWindow");
            field.setAccessible(true);
            return (DateRange) field.get(ticket);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private java.time.Instant ticketIssuedAt(Ticket ticket) {
        try {
            var field = Ticket.class.getDeclaredField("issuedAt");
            field.setAccessible(true);
            return (java.time.Instant) field.get(ticket);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
