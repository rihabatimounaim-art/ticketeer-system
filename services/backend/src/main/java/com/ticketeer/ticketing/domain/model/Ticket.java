package com.ticketeer.ticketing.domain.model;

import com.ticketeer.identity.domain.model.UserId;
import com.ticketeer.shared.domain.exception.BusinessRuleViolationException;
import com.ticketeer.shared.domain.model.DateRange;

import java.time.Instant;
import java.util.Objects;

/**
 * Ticket aggregate root.
 */
public class Ticket {

    private final TicketId id;
    private final UserId holderId;
    private final DateRange validityWindow;
    private TicketStatus status;
    private final Instant issuedAt;

    public Ticket(
            final TicketId id,
            final UserId holderId,
            final DateRange validityWindow,
            final Instant issuedAt
    ) {
        if (id == null) throw new BusinessRuleViolationException("TicketId is required");
        if (holderId == null) throw new BusinessRuleViolationException("HolderId is required");
        if (validityWindow == null) throw new BusinessRuleViolationException("Validity window is required");
        if (issuedAt == null) throw new BusinessRuleViolationException("IssuedAt is required");

        this.id = id;
        this.holderId = holderId;
        this.validityWindow = validityWindow;
        this.issuedAt = issuedAt;
        this.status = TicketStatus.CREATED;
    }

    public TicketId getId() {
        return id;
    }

    public UserId getHolderId() {
        return holderId;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public boolean isValidAt(final Instant instant) {
        if (instant == null) {
            throw new BusinessRuleViolationException("Instant must not be null");
        }
        return validityWindow.contains(instant) && status == TicketStatus.VALID;
    }

    public void activate() {
        if (this.status != TicketStatus.CREATED) {
            throw new BusinessRuleViolationException("Only CREATED ticket can be activated");
        }
        this.status = TicketStatus.VALID;
    }

    public void expire(final Instant now) {
        if (!validityWindow.contains(now)) {
            this.status = TicketStatus.EXPIRED;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ticket)) return false;
        Ticket ticket = (Ticket) o;
        return id.equals(ticket.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
