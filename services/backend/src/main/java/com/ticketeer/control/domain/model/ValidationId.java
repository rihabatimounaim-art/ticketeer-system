package com.ticketeer.control.domain.model;

import com.ticketeer.shared.domain.exception.BusinessRuleViolationException;

import java.util.Objects;
import java.util.UUID;

/**
 * Strongly typed identifier for validation records.
 */
public final class ValidationId {

    private final UUID value;

    public ValidationId(final UUID value) {
        if (value == null) {
            throw new BusinessRuleViolationException("ValidationId must not be null");
        }
        this.value = value;
    }

    public static ValidationId newId() {
        return new ValidationId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ValidationId)) return false;
        final ValidationId that = (ValidationId) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
