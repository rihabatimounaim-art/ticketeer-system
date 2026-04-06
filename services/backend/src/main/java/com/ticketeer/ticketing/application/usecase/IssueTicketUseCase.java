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
                command.departureStationCode(),
                command.arrivalStationCode(),
                command.departureTime(),
                command.arrivalTime(),
                command.price(),
                clock.now()
        );

        ticket.activate();

        final String payload = buildPayload(ticket);
        final String signature = signatureService.sign(payload);
        final String qrContent = qrCodeGenerator.generate(payload, signature);

        if (qrContent == null || qrContent.isBlank()) {
            throw new IllegalStateException("QR content generation failed");
        }

        return ticketRepository.save(ticket);
    }

    private String buildPayload(final Ticket ticket) {
        return "ticketId=" + ticket.getId()
                + ";holderId=" + ticket.getHolderId()
                + ";from=" + ticket.getDepartureStationCode()
                + ";to=" + ticket.getArrivalStationCode()
                + ";departureTime=" + ticket.getDepartureTime()
                + ";arrivalTime=" + ticket.getArrivalTime()
                + ";price=" + ticket.getPrice()
                + ";validFrom=" + ticket.getValidityWindow().getStart()
                + ";validUntil=" + ticket.getValidityWindow().getEnd()
                + ";issuedAt=" + ticket.getIssuedAt();
    }
}
