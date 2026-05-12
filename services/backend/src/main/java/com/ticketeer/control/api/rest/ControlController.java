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

    @Operation(
            summary = "Validate a ticket",
            description = "Returns VALID, EXPIRED, ALREADY_CONTROLLED, WRONG_ROUTE, TOO_EARLY, or NOT_FOUND"
    )
    @PostMapping("/validate")
    public ValidateTicketResponse validate(
            @Valid @RequestBody final ValidateTicketRequest request,
            @AuthenticationPrincipal final JwtAuthenticatedUser authenticatedUser
    ) {
        final ValidationRecord record = validateTicketUseCase.execute(
                new TicketId(request.ticketId()),
                new UserId(UUID.fromString(authenticatedUser.userId())),
                request.departureStationCode(),
                request.arrivalStationCode()    
        );

        return new ValidateTicketResponse(
                request.ticketId(),
                record.getResult().name(),
                record.getResult().name(),
                buildMessage(record.getResult().name())
        );
    }

    private String buildMessage(String result) {
        return switch (result) {
            case "VALID" -> "Billet valide.";
            case "ALREADY_CONTROLLED" -> "Billet valide, mais déjà contrôlé.";
            case "EXPIRED" -> "Billet non valide : période de validité dépassée.";
            case "TOO_EARLY" -> "Billet non valide : contrôle effectué trop tôt.";
            case "WRONG_ROUTE" -> "Billet non valide : ce billet ne correspond pas au trajet contrôlé.";
            case "NOT_FOUND" -> "Billet non valide : billet inexistant.";
            default -> "Billet non valide.";
        };
    }
}