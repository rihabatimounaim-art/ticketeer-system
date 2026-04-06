package com.ticketeer.ticketing.application.usecase;

import com.ticketeer.identity.domain.model.UserId;
import com.ticketeer.ticketing.application.port.QrCodeGenerator;
import com.ticketeer.ticketing.application.port.QrImageGenerator;
import com.ticketeer.ticketing.application.port.SignatureService;
import com.ticketeer.ticketing.application.port.TicketRepository;
import com.ticketeer.ticketing.domain.model.Ticket;
import com.ticketeer.ticketing.domain.model.TicketId;

public class GenerateTicketQrUseCase {

    private final TicketRepository ticketRepository;
    private final SignatureService signatureService;
    private final QrCodeGenerator qrCodeGenerator;
    private final QrImageGenerator qrImageGenerator;

    public GenerateTicketQrUseCase(final TicketRepository ticketRepository,
                                   final SignatureService signatureService,
                                   final QrCodeGenerator qrCodeGenerator,
                                   final QrImageGenerator qrImageGenerator) {
        this.ticketRepository = ticketRepository;
        this.signatureService = signatureService;
        this.qrCodeGenerator = qrCodeGenerator;
        this.qrImageGenerator = qrImageGenerator;
    }

    public byte[] execute(final TicketId ticketId, final UserId requesterId, final boolean isAdmin) {
        final Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (!isAdmin && !ticket.getHolderId().equals(requesterId)) {
            throw new RuntimeException("Forbidden");
        }

        final String payload = buildPayload(ticket);
        final String signature = signatureService.sign(payload);
        final String qrContent = qrCodeGenerator.generate(payload, signature);

        return qrImageGenerator.generatePng(qrContent, 300, 300);
    }

    private String buildPayload(final Ticket ticket) {
        return "ticketId=" + ticket.getId()
                + ";holderId=" + ticket.getHolderId()
                + ";validFrom=" + ticket.getValidityWindow().getStart()
                + ";validUntil=" + ticket.getValidityWindow().getEnd()
                + ";issuedAt=" + ticket.getIssuedAt();
    }
}
