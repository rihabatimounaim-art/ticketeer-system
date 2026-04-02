package com.ticketeer.ticketing.application.command;

import com.ticketeer.identity.domain.model.UserId;
import com.ticketeer.shared.domain.model.DateRange;

/**
 * Command used to issue a new ticket.
 */
public record IssueTicketCommand(
        UserId holderId,
        DateRange validityWindow
) {
}
