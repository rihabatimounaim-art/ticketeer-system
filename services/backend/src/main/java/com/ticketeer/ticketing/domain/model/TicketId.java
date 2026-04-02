package com.ticketeer.ticketing.domain.model;

import com.ticketeer.shared.domain.exception.BusinessRuleViolationException;

import java.util.Objects;
import java.util.UUID;

/**
 * Strongly typed identifier for tickets.
 */
public final class TicketId {

    private final UUID value;

    public TicketId(final UUID value) {
        if (value == null) {
            throw new BusinessRuleViolationException("TicketId must not be null");
        }
        this.value = value;
    }

    public static TicketId newId() {
        return new TicketId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof TicketId)) return false;
        final TicketId ticketId = (TicketId) o;
        return value.equals(ticketId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
