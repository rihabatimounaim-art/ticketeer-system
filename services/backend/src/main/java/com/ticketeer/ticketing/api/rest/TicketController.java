package com.ticketeer.ticketing.api.rest;

import com.ticketeer.identity.domain.model.UserId;
import com.ticketeer.security.JwtAuthenticatedUser;
import com.ticketeer.shared.domain.model.DateRange;
import com.ticketeer.ticketing.application.command.IssueTicketCommand;
import com.ticketeer.ticketing.application.usecase.GenerateTicketPdfUseCase;
import com.ticketeer.ticketing.application.usecase.GenerateTicketQrUseCase;
import com.ticketeer.ticketing.application.usecase.GetMyTicketsUseCase;
import com.ticketeer.ticketing.application.usecase.GetTicketHistoryUseCase;
import com.ticketeer.ticketing.application.usecase.IssueTicketResult;
import com.ticketeer.ticketing.application.usecase.IssueTicketUseCase;
import com.ticketeer.ticketing.domain.model.Ticket;
import com.ticketeer.ticketing.domain.model.TicketId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Tag(name = "Tickets", description = "Ticket purchase and retrieval")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final IssueTicketUseCase issueTicketUseCase;
    private final GetMyTicketsUseCase getMyTicketsUseCase;
    private final GetTicketHistoryUseCase getTicketHistoryUseCase;
    private final GenerateTicketQrUseCase generateTicketQrUseCase;
    private final GenerateTicketPdfUseCase generateTicketPdfUseCase;

    public TicketController(final IssueTicketUseCase issueTicketUseCase,
                            final GetMyTicketsUseCase getMyTicketsUseCase,
                            final GetTicketHistoryUseCase getTicketHistoryUseCase,
                            final GenerateTicketQrUseCase generateTicketQrUseCase,
                            final GenerateTicketPdfUseCase generateTicketPdfUseCase) {
        this.issueTicketUseCase = issueTicketUseCase;
        this.getMyTicketsUseCase = getMyTicketsUseCase;
        this.getTicketHistoryUseCase = getTicketHistoryUseCase;
        this.generateTicketQrUseCase = generateTicketQrUseCase;
        this.generateTicketPdfUseCase = generateTicketPdfUseCase;
    }

    @Operation(summary = "Purchase a ticket (discount applied server-side)")
    @PostMapping
    public CreateTicketResponse createTicket(
            @Valid @RequestBody final CreateTicketRequest request,
            @AuthenticationPrincipal final JwtAuthenticatedUser authenticatedUser) {

        final UserId holderId = authenticatedUser != null
                ? new UserId(UUID.fromString(authenticatedUser.userId()))
                : new UserId(UUID.fromString(request.holderId()));

        final IssueTicketResult result = issueTicketUseCase.execute(
                new IssueTicketCommand(
                        holderId,
                        new DateRange(
                                Instant.parse(request.validFrom()),
                                Instant.parse(request.validUntil())
                        ),
                        request.departureStationCode(),
                        request.arrivalStationCode(),
                        Instant.parse(request.departureTime()),
                        Instant.parse(request.arrivalTime()),
                        request.price()
                )
        );

        final Ticket ticket = result.ticket();
        final var discount = result.discount();

        return new CreateTicketResponse(
                ticket.getId().toString(),
                ticket.getStatus().name(),
                discount.finalPrice(),
                discount.originalPrice(),
                discount.discountPercent(),
                discount.discountLabel()
        );
    }

    @Operation(summary = "List my active tickets")
    @GetMapping("/me")
    public List<MyTicketResponse> getMyTickets(
            @AuthenticationPrincipal final JwtAuthenticatedUser authenticatedUser) {
        final UserId holderId = new UserId(UUID.fromString(authenticatedUser.userId()));
        return getMyTicketsUseCase.execute(holderId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Operation(summary = "List my past tickets (historique)")
    @GetMapping("/me/history")
    public List<MyTicketResponse> getTicketHistory(
            @AuthenticationPrincipal final JwtAuthenticatedUser authenticatedUser) {
        final UserId holderId = new UserId(UUID.fromString(authenticatedUser.userId()));
        return getTicketHistoryUseCase.execute(holderId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Operation(summary = "Download QR code for a ticket")
    @GetMapping("/{ticketId}/qr")
    public ResponseEntity<byte[]> downloadTicketQr(
            @PathVariable final String ticketId,
            @AuthenticationPrincipal final JwtAuthenticatedUser authenticatedUser) {
        final UserId requesterId = new UserId(UUID.fromString(authenticatedUser.userId()));
        final boolean isAdmin = "ADMIN".equalsIgnoreCase(authenticatedUser.role());

        final byte[] qrPng = generateTicketQrUseCase.execute(
                new TicketId(UUID.fromString(ticketId)), requesterId, isAdmin
        );

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentDisposition(ContentDisposition.inline()
                .filename("ticket-" + ticketId + "-qr.png").build());

        return ResponseEntity.ok().headers(headers).body(qrPng);
    }

    @Operation(summary = "Download PDF for a ticket")
    @GetMapping("/{ticketId}/pdf")
    public ResponseEntity<byte[]> downloadTicketPdf(
            @PathVariable final String ticketId,
            @AuthenticationPrincipal final JwtAuthenticatedUser authenticatedUser) {
        final UserId requesterId = new UserId(UUID.fromString(authenticatedUser.userId()));
        final boolean isAdmin = "ADMIN".equalsIgnoreCase(authenticatedUser.role());

        final byte[] pdfBytes = generateTicketPdfUseCase.execute(
                new TicketId(UUID.fromString(ticketId)), requesterId, isAdmin
        );

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("ticket-" + ticketId + ".pdf").build());

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    // ---- helper ----

    private MyTicketResponse toResponse(final Ticket ticket) {
        return new MyTicketResponse(
                ticket.getId().toString(),
                ticket.getHolderId().toString(),
                ticket.getDepartureStationCode(),
                ticket.getArrivalStationCode(),
                ticket.getDepartureTime().toString(),
                ticket.getArrivalTime().toString(),
                ticket.getPrice(),
                ticket.getValidityWindow().getStart().toString(),
                ticket.getValidityWindow().getEnd().toString(),
                ticket.getStatus().name(),
                ticket.getIssuedAt().toString()
        );
    }
}
