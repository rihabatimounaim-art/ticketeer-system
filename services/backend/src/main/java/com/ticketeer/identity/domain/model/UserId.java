package com.ticketeer.identity.domain.model;

import com.ticketeer.shared.domain.exception.BusinessRuleViolationException;

import java.util.Objects;
import java.util.UUID;

/**
 * Strongly typed identifier for users.
 */
public final class UserId {

    private final UUID value;

    public UserId(final UUID value) {
        if (value == null) {
            throw new BusinessRuleViolationException("UserId must not be null");
        }
        this.value = value;
    }

    public static UserId newId() {
        return new UserId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof UserId)) return false;
        final UserId userId = (UserId) o;
        return value.equals(userId.value);
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
