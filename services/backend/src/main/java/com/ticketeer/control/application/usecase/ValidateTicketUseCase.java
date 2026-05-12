package com.ticketeer.control.application.usecase;

import com.ticketeer.control.application.port.ValidationRepository;
import com.ticketeer.control.domain.model.ValidationId;
import com.ticketeer.control.domain.model.ValidationRecord;
import com.ticketeer.control.domain.model.ValidationResult;
import com.ticketeer.identity.domain.model.UserId;
import com.ticketeer.shared.domain.time.DomainClock;
import com.ticketeer.ticketing.application.port.TicketRepository;
import com.ticketeer.ticketing.domain.model.Ticket;
import com.ticketeer.ticketing.domain.model.TicketId;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

    public ValidationRecord execute(
            final TicketId ticketId,
            final UserId agentId,
            final String departureStationCode,
            final String arrivalStationCode
    ) {
        final Instant now = clock.now();

        final Ticket ticket = ticketRepository.findById(ticketId).orElse(null);

        final ValidationResult result;

        if (ticket == null) {
            result = ValidationResult.NOT_FOUND;
        } else {
            result = determineValidationResult(
                    ticket,
                    ticketId,
                    now,
                    departureStationCode,
                    arrivalStationCode
            );
        }

        final ValidationRecord record = new ValidationRecord(
                ValidationId.newId(),
                ticketId,
                agentId,
                now,
                result
        );

        return validationRepository.save(record);
    }

    private ValidationResult determineValidationResult(
            final Ticket ticket,
            final TicketId ticketId,
            final Instant now,
            final String departureStationCode,
            final String arrivalStationCode
    ) {
        if (!ticket.getDepartureStationCode().equalsIgnoreCase(departureStationCode)
                || !ticket.getArrivalStationCode().equalsIgnoreCase(arrivalStationCode)) {
            return ValidationResult.WRONG_ROUTE;
        }

        final Instant validFrom = ticket.getDepartureTime().minus(30, ChronoUnit.MINUTES);
        final Instant validUntil = ticket.getArrivalTime().plus(30, ChronoUnit.MINUTES);

        if (now.isBefore(validFrom)) {
            return ValidationResult.TOO_EARLY;
        }

        if (now.isAfter(validUntil)) {
            return ValidationResult.EXPIRED;
        }

        final List<ValidationRecord> previousValidations =
                validationRepository.findByTicketId(ticketId);

        if (!previousValidations.isEmpty()) {
            return ValidationResult.ALREADY_CONTROLLED;
        }

        return ValidationResult.VALID;
    }
}