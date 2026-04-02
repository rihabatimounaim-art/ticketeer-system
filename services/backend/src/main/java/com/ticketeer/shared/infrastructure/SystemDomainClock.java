package com.ticketeer.shared.infrastructure;

import com.ticketeer.shared.domain.time.DomainClock;

import java.time.Instant;

public class SystemDomainClock implements DomainClock {

    @Override
    public Instant now() {
        return Instant.now();
    }
}
