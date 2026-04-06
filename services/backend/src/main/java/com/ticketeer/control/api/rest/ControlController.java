package com.ticketeer.control.api.rest;

import com.ticketeer.control.application.usecase.ValidateTicketUseCase;
import com.ticketeer.control.domain.model.ValidationRecord;
import com.ticketeer.identity.domain.model.UserId;
import com.ticketeer.security.JwtAuthenticatedUser;
import com.ticketeer.ticketing.domain.model.TicketId;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/control")
public class ControlController {

    private final ValidateTicketUseCase validateTicketUseCase;

    public ControlController(final ValidateTicketUseCase validateTicketUseCase) {
        this.validateTicketUseCase = validateTicketUseCase;
    }

    @PostMapping("/validate")
    public ValidateTicketResponse validate(@RequestBody final ValidateTicketRequest request,
                                           @AuthenticationPrincipal final JwtAuthenticatedUser authenticatedUser) {

        final ValidationRecord record = validateTicketUseCase.execute(
                new TicketId(UUID.fromString(request.ticketId())),
                new UserId(UUID.fromString(authenticatedUser.userId()))
        );

        return new ValidateTicketResponse(record.getResult().name());
    }
}
