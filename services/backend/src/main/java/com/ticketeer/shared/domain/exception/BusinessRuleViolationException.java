package com.ticketeer.shared.domain.exception;

/**
 * Thrown when a business rule is violated.
 */
public class BusinessRuleViolationException extends DomainException {

    public BusinessRuleViolationException(final String message) {
        super(message);
    }
}
