package com.ticketeer.ticketing.application.usecase;

import com.ticketeer.ticketing.domain.model.Ticket;
import com.ticketeer.ticketing.domain.service.DiscountResult;

/**
 * Holds the created ticket and the discount that was applied.
 */
public record IssueTicketResult(
        Ticket ticket,
        DiscountResult discount
) {
}
