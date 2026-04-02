package com.ticketeer.control.application.port;

import com.ticketeer.control.domain.model.ValidationRecord;
import com.ticketeer.ticketing.domain.model.TicketId;

import java.util.List;

/**
 * Port for accessing validation history.
 */
public interface ValidationRepository {

    ValidationRecord save(ValidationRecord record);

    List<ValidationRecord> findByTicketId(TicketId ticketId);
}
