package com.ticketeer.ticketing.application.usecase;

import com.ticketeer.identity.domain.model.UserId;
import com.ticketeer.ticketing.application.port.TicketRepository;
import com.ticketeer.ticketing.domain.model.Ticket;

import java.util.List;

/**
 * Use case responsible for retrieving tickets of the authenticated user.
 */
public class GetMyTicketsUseCase {

    private final TicketRepository ticketRepository;

    public GetMyTicketsUseCase(final TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<Ticket> execute(final UserId holderId) {
        return ticketRepository.findByHolderId(holderId);
    }
}
