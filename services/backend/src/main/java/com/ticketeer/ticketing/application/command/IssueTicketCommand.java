package com.ticketeer.ticketing.application.command;

import com.ticketeer.identity.domain.model.UserId;
import com.ticketeer.shared.domain.model.DateRange;

import java.time.Instant;

/**
 * Command used to issue a new ticket.
 */
public record IssueTicketCommand(
        UserId holderId,
        DateRange validityWindow,
        String departureStationCode,
        String arrivalStationCode,
        Instant departureTime,
        Instant arrivalTime,
        double price
) {
}
