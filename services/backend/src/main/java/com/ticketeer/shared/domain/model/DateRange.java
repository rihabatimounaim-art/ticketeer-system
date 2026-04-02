package com.ticketeer.shared.domain.model;

import com.ticketeer.shared.domain.exception.BusinessRuleViolationException;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable value object representing a time interval.
 */
public final class DateRange {

    private final Instant start;
    private final Instant end;

    public DateRange(final Instant start, final Instant end) {
        if (start == null || end == null) {
            throw new BusinessRuleViolationException("Start and end must not be null");
        }
        if (start.isAfter(end)) {
            throw new BusinessRuleViolationException("Start must be before or equal to end");
        }

        this.start = start;
        this.end = end;
    }

    public Instant getStart() {
        return start;
    }

    public Instant getEnd() {
        return end;
    }

    public boolean contains(final Instant instant) {
        if (instant == null) {
            throw new BusinessRuleViolationException("Instant must not be null");
        }
        return !instant.isBefore(start) && !instant.isAfter(end);
    }

    public boolean overlaps(final DateRange other) {
        if (other == null) {
            throw new BusinessRuleViolationException("Other range must not be null");
        }
        return !this.end.isBefore(other.start) && !other.end.isBefore(this.start);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DateRange)) return false;
        DateRange that = (DateRange) o;
        return start.equals(that.start) && end.equals(that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}
