package com.ticketeer.control.infrastructure.persistence;

import com.ticketeer.control.application.port.ValidationRepository;
import com.ticketeer.control.domain.model.ValidationId;
import com.ticketeer.control.domain.model.ValidationRecord;
import com.ticketeer.control.domain.model.ValidationResult;
import com.ticketeer.identity.domain.model.UserId;
import com.ticketeer.ticketing.domain.model.TicketId;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary
public class JpaValidationRepositoryAdapter implements ValidationRepository {

    private final SpringDataValidationRepository repository;

    public JpaValidationRepositoryAdapter(final SpringDataValidationRepository repository) {
        this.repository = repository;
    }

    @Override
    public ValidationRecord save(final ValidationRecord record) {
        final ValidationRecordEntity entity = new ValidationRecordEntity(
                record.getId().getValue(),
                record.getTicketId().getValue(),
                record.getAgentId().getValue(),
                record.getValidatedAt(),
                record.getResult().name()
        );

        final ValidationRecordEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<ValidationRecord> findByTicketId(final TicketId ticketId) {
        return repository.findByTicketId(ticketId.getValue())
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private ValidationRecord toDomain(final ValidationRecordEntity entity) {
        return new ValidationRecord(
                new ValidationId(entity.getId()),
                new TicketId(entity.getTicketId()),
                new UserId(entity.getAgentId()),
                entity.getValidatedAt(),
                ValidationResult.valueOf(entity.getResult())
        );
    }
}
