package com.ticketeer.shared.domain.time;

import java.time.Instant;

/**
 * Abstraction over time to keep domain logic testable and deterministic.
 */
public interface DomainClock {

    Instant now();
}
