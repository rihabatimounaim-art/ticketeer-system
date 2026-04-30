package com.ticketeer.control.api.rest;

import com.ticketeer.control.application.usecase.ValidateTicketUseCase;
import com.ticketeer.control.domain.model.ValidationRecord;
import com.ticketeer.identity.domain.model.UserId;
import com.ticketeer.security.JwtAuthenticatedUser;
import com.ticketeer.ticketing.domain.model.TicketId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Control", description = "Ticket validation for inspectors")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/control")
public class ControlController {

    private final ValidateTicketUseCase validateTicketUseCase;

    public ControlController(final ValidateTicketUseCase validateTicketUseCase) {
        this.validateTicketUseCase = validateTicketUseCase;
    }

    @Operation(summary = "Validate a ticket", description = "Returns VALID, EXPIRED, or ALREADY_CONTROLLED")
    @PostMapping("/validate")
    public ValidateTicketResponse validate(@Valid @RequestBody final ValidateTicketRequest request,
                                           @AuthenticationPrincipal final JwtAuthenticatedUser authenticatedUser) {
        final ValidationRecord record = validateTicketUseCase.execute(
                new TicketId(UUID.fromString(request.ticketId())),
                new UserId(UUID.fromString(authenticatedUser.userId()))
        );
        return new ValidateTicketResponse(record.getResult().name());
    }
}
