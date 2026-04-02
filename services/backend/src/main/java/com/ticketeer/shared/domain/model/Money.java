package com.ticketeer.shared.domain.model;

import com.ticketeer.shared.domain.exception.BusinessRuleViolationException;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Immutable value object representing a monetary amount.
 */
public final class Money {

    private final BigDecimal amount;
    private final String currency;

    public Money(final BigDecimal amount, final String currency) {
        if (amount == null) {
            throw new BusinessRuleViolationException("Amount must not be null");
        }
        if (currency == null || currency.isBlank()) {
            throw new BusinessRuleViolationException("Currency must not be null or blank");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleViolationException("Amount must be non-negative");
        }

        this.amount = amount;
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Money add(final Money other) {
        requireSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money multiply(final BigDecimal factor) {
        if (factor == null) {
            throw new BusinessRuleViolationException("Factor must not be null");
        }
        return new Money(this.amount.multiply(factor), this.currency);
    }

    private void requireSameCurrency(final Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new BusinessRuleViolationException("Currency mismatch");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money)) return false;
        Money money = (Money) o;
        return amount.equals(money.amount) && currency.equals(money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
}
