package com.ticketeer.ticketing.application.usecase;

import com.ticketeer.identity.domain.model.UserId;
import com.ticketeer.shared.domain.time.DomainClock;
import com.ticketeer.ticketing.application.port.TicketRepository;
import com.ticketeer.ticketing.domain.model.Ticket;

import java.util.Comparator;
import java.util.List;

/**
 * Use case returning past (expired) tickets for a given user.
 * A ticket is "past" when its validity window has ended.
 */
public class GetTicketHistoryUseCase {

    private final TicketRepository ticketRepository;
    private final DomainClock clock;

    public GetTicketHistoryUseCase(final TicketRepository ticketRepository,
                                   final DomainClock clock) {
        this.ticketRepository = ticketRepository;
        this.clock = clock;
    }

    public List<Ticket> execute(final UserId holderId) {
        final var now = clock.now();
        return ticketRepository.findByHolderId(holderId)
                .stream()
                .filter(t -> t.getValidityWindow().getEnd().isBefore(now))
                .sorted(Comparator.comparing((Ticket t) -> t.getValidityWindow().getEnd())
                        .reversed())
                .toList();
    }
}
