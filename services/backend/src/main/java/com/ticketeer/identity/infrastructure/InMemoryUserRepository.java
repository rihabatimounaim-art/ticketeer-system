package com.ticketeer.control.infrastructure;

import com.ticketeer.control.application.port.ValidationRepository;
import com.ticketeer.control.domain.model.ValidationRecord;
import com.ticketeer.ticketing.domain.model.TicketId;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryValidationRepository implements ValidationRepository {

    private final Map<TicketId, List<ValidationRecord>> storage = new HashMap<>();

    @Override
    public ValidationRecord save(ValidationRecord record) {
        storage.computeIfAbsent(record.getTicketId(), k -> new ArrayList<>())
               .add(record);
        return record;
    }

    @Override
    public List<ValidationRecord> findByTicketId(TicketId ticketId) {
        return storage.getOrDefault(ticketId, Collections.emptyList());
    }
}
