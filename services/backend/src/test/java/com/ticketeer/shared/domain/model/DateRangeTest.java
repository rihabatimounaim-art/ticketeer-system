package com.ticketeer.shared.domain.model;

import com.ticketeer.shared.domain.exception.BusinessRuleViolationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DateRangeTest {

    private final Instant t1 = Instant.parse("2026-05-01T08:00:00Z");
    private final Instant t2 = Instant.parse("2026-05-01T10:00:00Z");
    private final Instant t3 = Instant.parse("2026-05-01T12:00:00Z");

    @Test
    void should_contain_instant_within_range() {
        final DateRange range = new DateRange(t1, t3);
        assertThat(range.contains(t2)).isTrue();
    }

    @Test
    void should_contain_start_and_end() {
        final DateRange range = new DateRange(t1, t3);
        assertThat(range.contains(t1)).isTrue();
        assertThat(range.contains(t3)).isTrue();
    }

    @Test
    void should_not_contain_instant_outside_range() {
        final DateRange range = new DateRange(t2, t3);
        assertThat(range.contains(t1)).isFalse();
    }

    @Test
    void should_throw_when_start_after_end() {
        assertThatThrownBy(() -> new DateRange(t3, t1))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("Start must be before");
    }

    @Test
    void should_throw_when_null_start() {
        assertThatThrownBy(() -> new DateRange(null, t1))
                .isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    void should_detect_overlap() {
        final DateRange r1 = new DateRange(t1, t2);
        final DateRange r2 = new DateRange(t2, t3);
        assertThat(r1.overlaps(r2)).isTrue();
    }

    @Test
    void should_detect_no_overlap() {
        final DateRange r1 = new DateRange(t1, t1.plusSeconds(1));
        final DateRange r2 = new DateRange(t3.minusSeconds(1), t3);
        assertThat(r1.overlaps(r2)).isFalse();
    }
}
