package com.ticketeer.control.domain.model;

/**
 * Result of a ticket validation.
 */
public enum ValidationResult {
    VALID,
    ALREADY_CONTROLLED,
    EXPIRED,
    NOT_FOUND,
    WRONG_ROUTE,
    TOO_EARLY
}
