package com.ticketeer.control.domain.model;

import com.ticketeer.identity.domain.model.UserId;
import com.ticketeer.shared.domain.exception.BusinessRuleViolationException;
import com.ticketeer.ticketing.domain.model.TicketId;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a control operation performed by an agent.
 */
public class ValidationRecord {

    private final ValidationId id;
    private final TicketId ticketId;
    private final UserId agentId;
    private final Instant validatedAt;
    private final ValidationResult result;

    public ValidationRecord(
            final ValidationId id,
            final TicketId ticketId,
            final UserId agentId,
            final Instant validatedAt,
            final ValidationResult result
    ) {
        if (id == null) throw new BusinessRuleViolationException("ValidationId is required");
        if (ticketId == null) throw new BusinessRuleViolationException("TicketId is required");
        if (agentId == null) throw new BusinessRuleViolationException("AgentId is required");
        if (validatedAt == null) throw new BusinessRuleViolationException("Validation time is required");
        if (result == null) throw new BusinessRuleViolationException("Validation result is required");

        this.id = id;
        this.ticketId = ticketId;
        this.agentId = agentId;
        this.validatedAt = validatedAt;
        this.result = result;
    }

    public ValidationResult getResult() {
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ValidationRecord)) return false;
        ValidationRecord that = (ValidationRecord) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
