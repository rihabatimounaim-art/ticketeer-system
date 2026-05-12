package com.ticketeer.ticketing.application.usecase;

import com.ticketeer.shared.domain.time.DomainClock;
import com.ticketeer.ticketing.application.command.IssueTicketCommand;
import com.ticketeer.ticketing.application.port.QrCodeGenerator;
import com.ticketeer.ticketing.application.port.SignatureService;
import com.ticketeer.ticketing.application.port.TicketRepository;
import com.ticketeer.ticketing.domain.model.Ticket;
import com.ticketeer.ticketing.domain.model.TicketId;
import com.ticketeer.ticketing.domain.service.DiscountResult;
import com.ticketeer.ticketing.domain.service.SeasonDiscountPolicy;

import java.time.Instant;

/**
 * Use case responsible for issuing a ticket with discount applied.
 */
public class IssueTicketUseCase {

    private final TicketRepository ticketRepository;
    private final QrCodeGenerator qrCodeGenerator;
    private final SignatureService signatureService;
    private final DomainClock clock;
    private final SeasonDiscountPolicy discountPolicy;

    public IssueTicketUseCase(
            final TicketRepository ticketRepository,
            final QrCodeGenerator qrCodeGenerator,
            final SignatureService signatureService,
            final DomainClock clock,
            final SeasonDiscountPolicy discountPolicy
    ) {
        this.ticketRepository = ticketRepository;
        this.qrCodeGenerator = qrCodeGenerator;
        this.signatureService = signatureService;
        this.clock = clock;
        this.discountPolicy = discountPolicy;
    }

    public IssueTicketResult execute(final IssueTicketCommand command) {
        final Instant now = clock.now();

        // Apply season / weekend / advance discount
        final DiscountResult discount = discountPolicy.apply(
                command.price(),
                command.departureTime(),
                now
        );

        final Ticket ticket = new Ticket(
                TicketId.newId(),
                command.holderId(),
                command.validityWindow(),
                command.departureStationCode(),
                command.arrivalStationCode(),
                command.departureTime(),
                command.arrivalTime(),
                discount.finalPrice(),
                now
        );

        ticket.activate();

        final String payload = buildPayload(ticket);
        final String signature = signatureService.sign(payload);
        final String qrContent = qrCodeGenerator.generate(payload, signature);

        if (qrContent == null || qrContent.isBlank()) {
            throw new IllegalStateException("QR content generation failed");
        }

        final Ticket saved = ticketRepository.save(ticket);
        return new IssueTicketResult(saved, discount);
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
