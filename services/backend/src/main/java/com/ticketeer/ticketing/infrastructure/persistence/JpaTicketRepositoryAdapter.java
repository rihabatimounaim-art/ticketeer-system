package com.ticketeer.ticketing.infrastructure.persistence;

import com.ticketeer.identity.domain.model.UserId;
import com.ticketeer.shared.domain.model.DateRange;
import com.ticketeer.ticketing.application.port.TicketRepository;
import com.ticketeer.ticketing.domain.model.Ticket;
import com.ticketeer.ticketing.domain.model.TicketId;
import com.ticketeer.ticketing.domain.model.TicketStatus;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@Primary
public class JpaTicketRepositoryAdapter implements TicketRepository {

    private final SpringDataTicketRepository repository;

    public JpaTicketRepositoryAdapter(final SpringDataTicketRepository repository) {
        this.repository = repository;
    }

    @Override
    public Ticket save(final Ticket ticket) {
        final DateRange validityWindow = ticket.getValidityWindow();
        final Instant issuedAt = ticket.getIssuedAt();

        final TicketEntity entity = new TicketEntity(
                ticket.getId().getValue(),
                ticket.getHolderId().getValue(),
                validityWindow.getStart(),
                validityWindow.getEnd(),
                ticket.getDepartureStationCode(),
                ticket.getArrivalStationCode(),
                ticket.getDepartureTime(),
                ticket.getArrivalTime(),
                ticket.getPrice(),
                ticket.getStatus().name(),
                issuedAt
        );

        final TicketEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Ticket> findById(final TicketId ticketId) {
        return repository.findById(ticketId.getValue())
                .map(this::toDomain);
    }

    @Override
    public List<Ticket> findByHolderId(final UserId holderId) {
        return repository.findByHolderId(holderId.getValue())
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private Ticket toDomain(final TicketEntity entity) {
        return Ticket.rehydrate(
                new TicketId(entity.getId()),
                new UserId(entity.getHolderId()),
                new DateRange(entity.getValidFrom(), entity.getValidUntil()),
                entity.getDepartureStationCode(),
                entity.getArrivalStationCode(),
                entity.getDepartureTime(),
                entity.getArrivalTime(),
                entity.getPrice(),
                entity.getIssuedAt(),
                TicketStatus.valueOf(entity.getStatus())
        );
    }
}
