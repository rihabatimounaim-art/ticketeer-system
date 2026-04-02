package com.ticketeer.ticketing.api.rest;

import com.ticketeer.identity.domain.model.UserId;
import com.ticketeer.shared.domain.model.DateRange;
import com.ticketeer.ticketing.application.command.IssueTicketCommand;
import com.ticketeer.ticketing.application.usecase.IssueTicketUseCase;
import com.ticketeer.ticketing.domain.model.Ticket;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final IssueTicketUseCase issueTicketUseCase;

    public TicketController(final IssueTicketUseCase issueTicketUseCase) {
        this.issueTicketUseCase = issueTicketUseCase;
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
}
