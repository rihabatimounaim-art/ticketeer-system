package com.ticketeer.control.domain.model;

/**
 * Result of a ticket validation.
 */
public enum ValidationResult {
    VALID,
    INVALID,
    EXPIRED,
    ALREADY_CONTROLLED
}
