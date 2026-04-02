package com.ticketeer.ticketing.application.usecase;

import com.ticketeer.shared.domain.time.DomainClock;
import com.ticketeer.ticketing.application.command.IssueTicketCommand;
import com.ticketeer.ticketing.application.port.QrCodeGenerator;
import com.ticketeer.ticketing.application.port.SignatureService;
import com.ticketeer.ticketing.application.port.TicketRepository;
import com.ticketeer.ticketing.domain.model.Ticket;
import com.ticketeer.ticketing.domain.model.TicketId;

/**
 * Use case responsible for issuing a ticket.
 */
public class IssueTicketUseCase {

    private final TicketRepository ticketRepository;
    private final QrCodeGenerator qrCodeGenerator;
    private final SignatureService signatureService;
    private final DomainClock clock;

    public IssueTicketUseCase(
            final TicketRepository ticketRepository,
            final QrCodeGenerator qrCodeGenerator,
            final SignatureService signatureService,
            final DomainClock clock
    ) {
        this.ticketRepository = ticketRepository;
        this.qrCodeGenerator = qrCodeGenerator;
        this.signatureService = signatureService;
        this.clock = clock;
    }

    public Ticket execute(final IssueTicketCommand command) {

        final Ticket ticket = new Ticket(
                TicketId.newId(),
                command.holderId(),
                command.validityWindow(),
                clock.now()
        );

        ticket.activate();

        final String qrPayload = qrCodeGenerator.generate(ticket);
        signatureService.sign(qrPayload);

        return ticketRepository.save(ticket);
    }
}
