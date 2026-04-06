package com.ticketeer.control.application.usecase;

import com.ticketeer.control.application.port.ValidationRepository;
import com.ticketeer.control.domain.model.ValidationId;
import com.ticketeer.control.domain.model.ValidationRecord;
import com.ticketeer.control.domain.model.ValidationResult;
import com.ticketeer.identity.domain.model.UserId;
import com.ticketeer.shared.domain.exception.BusinessRuleViolationException;
import com.ticketeer.shared.domain.time.DomainClock;
import com.ticketeer.ticketing.application.port.TicketRepository;
import com.ticketeer.ticketing.domain.model.Ticket;
import com.ticketeer.ticketing.domain.model.TicketId;

import java.time.Instant;
import java.util.List;

/**
 * Use case responsible for validating a ticket.
 */
public class ValidateTicketUseCase {

    private final TicketRepository ticketRepository;
    private final ValidationRepository validationRepository;
    private final DomainClock clock;

    public ValidateTicketUseCase(
            final TicketRepository ticketRepository,
            final ValidationRepository validationRepository,
            final DomainClock clock
    ) {
        this.ticketRepository = ticketRepository;
        this.validationRepository = validationRepository;
        this.clock = clock;
    }

    public ValidationRecord execute(final TicketId ticketId, final UserId agentId) {

        final Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new BusinessRuleViolationException("Ticket not found"));

        final Instant now = clock.now();
        final ValidationResult result = determineValidationResult(ticket, ticketId, now);

        final ValidationRecord record = new ValidationRecord(
                ValidationId.newId(),
                ticketId,
                agentId,
                now,
                result
        );

        return validationRepository.save(record);
    }

    private ValidationResult determineValidationResult(final Ticket ticket,
                                                       final TicketId ticketId,
                                                       final Instant now) {
        if (!ticket.isValidAt(now)) {
            return ValidationResult.EXPIRED;
        }

        final List<ValidationRecord> previousValidations = validationRepository.findByTicketId(ticketId);
        if (!previousValidations.isEmpty()) {
            return ValidationResult.ALREADY_CONTROLLED;
        }

        return ValidationResult.VALID;
    }
}
