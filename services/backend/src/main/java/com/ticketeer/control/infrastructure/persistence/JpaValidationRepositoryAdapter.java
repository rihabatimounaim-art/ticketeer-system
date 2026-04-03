package com.ticketeer.control.infrastructure.persistence;

import com.ticketeer.control.application.port.ValidationRepository;
import com.ticketeer.control.domain.model.*;
import com.ticketeer.identity.domain.model.UserId;
import com.ticketeer.ticketing.domain.model.TicketId;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Primary
public class JpaValidationRepositoryAdapter implements ValidationRepository {

    private final SpringDataValidationRepository repository;

    public JpaValidationRepositoryAdapter(SpringDataValidationRepository repository) {
        this.repository = repository;
    }
@Override
public ValidationRecord save(ValidationRecord record) {
    ValidationRecordEntity entity = new ValidationRecordEntity(
            record.getId().getValue(),
            record.getTicketId().getValue(),
            record.getAgentId().getValue(),
            record.getValidatedAt(),
            record.getResult().name()
    );

    ValidationRecordEntity saved = repository.save(entity);
    return toDomain(saved);
}

    @Override
    public List<ValidationRecord> findByTicketId(TicketId ticketId) {
        return repository.findByTicketId(ticketId.getValue())
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private ValidationRecord toDomain(ValidationRecordEntity entity) {
        return new ValidationRecord(
                new ValidationId(entity.getId()),
                new TicketId(entity.getTicketId()),
                new UserId(entity.getAgentId()),
                entity.getValidatedAt(),
                ValidationResult.valueOf(entity.getResult())
        );
    }
}
