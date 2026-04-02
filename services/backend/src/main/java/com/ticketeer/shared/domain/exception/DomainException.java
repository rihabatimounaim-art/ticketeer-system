package com.ticketeer.shared.domain.exception;

/**
 * Base exception type for domain-level business errors.
 * <p>
 * This exception intentionally carries no transport or infrastructure semantics.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(final String message) {
        super(message);
    }

    protected DomainException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
