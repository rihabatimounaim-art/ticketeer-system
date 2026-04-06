package com.ticketeer.ticketing.api.rest;

import com.ticketeer.identity.domain.model.UserId;
import com.ticketeer.security.JwtAuthenticatedUser;
import com.ticketeer.shared.domain.model.DateRange;
import com.ticketeer.ticketing.application.command.IssueTicketCommand;
import com.ticketeer.ticketing.application.usecase.GenerateTicketQrUseCase;
import com.ticketeer.ticketing.application.usecase.GetMyTicketsUseCase;
import com.ticketeer.ticketing.application.usecase.IssueTicketUseCase;
import com.ticketeer.ticketing.domain.model.Ticket;
import com.ticketeer.ticketing.domain.model.TicketId;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final IssueTicketUseCase issueTicketUseCase;
    private final GetMyTicketsUseCase getMyTicketsUseCase;
    private final GenerateTicketQrUseCase generateTicketQrUseCase;

    public TicketController(final IssueTicketUseCase issueTicketUseCase,
                            final GetMyTicketsUseCase getMyTicketsUseCase,
                            final GenerateTicketQrUseCase generateTicketQrUseCase) {
        this.issueTicketUseCase = issueTicketUseCase;
        this.getMyTicketsUseCase = getMyTicketsUseCase;
        this.generateTicketQrUseCase = generateTicketQrUseCase;
    }

    @PostMapping
    public CreateTicketResponse createTicket(@RequestBody final CreateTicketRequest request) {

        final Ticket ticket = issueTicketUseCase.execute(
                new IssueTicketCommand(
                        new UserId(UUID.fromString(request.holderId())),
                        new DateRange(
                                Instant.parse(request.validFrom()),
                                Instant.parse(request.validUntil())
                        )
                )
        );

        return new CreateTicketResponse(
                ticket.getId().toString(),
                ticket.getStatus().name()
        );
    }

    @GetMapping("/me")
    public List<MyTicketResponse> getMyTickets(@AuthenticationPrincipal final JwtAuthenticatedUser authenticatedUser) {
        final UserId holderId = new UserId(UUID.fromString(authenticatedUser.userId()));

        return getMyTicketsUseCase.execute(holderId)
                .stream()
                .map(ticket -> new MyTicketResponse(
                        ticket.getId().toString(),
                        ticket.getHolderId().toString(),
                        ticket.getValidityWindow().getStart().toString(),
                        ticket.getValidityWindow().getEnd().toString(),
                        ticket.getStatus().name(),
                        ticket.getIssuedAt().toString()
                ))
                .toList();
    }

    @GetMapping("/{ticketId}/qr")
    public ResponseEntity<byte[]> downloadTicketQr(@PathVariable final String ticketId) {
        final byte[] qrPng = generateTicketQrUseCase.execute(new TicketId(UUID.fromString(ticketId)));

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentDisposition(
                ContentDisposition.inline()
                        .filename("ticket-" + ticketId + "-qr.png")
                        .build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(qrPng);
    }
}
