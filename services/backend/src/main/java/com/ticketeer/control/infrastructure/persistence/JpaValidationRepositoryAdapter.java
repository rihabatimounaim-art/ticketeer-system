package com.ticketeer.control.infrastructure.persistence;

import com.ticketeer.control.domain.model.ValidationRecord;
import com.ticketeer.control.domain.model.ValidationResult;
import com.ticketeer.control.domain.model.ValidationRecordId;
import com.ticketeer.control.domain.model.ValidationRepository;
import com.ticketeer.ticketing.domain.model.TicketId;
import com.ticketeer.identity.domain.model.UserId;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@Primary
public class JpaValidationRepositoryAdapter implements ValidationRepository {

    private final SpringDataValidationRepository repository;

    public JpaValidationRepositoryAdapter(SpringDataValidationRepository repository) {
        this.repository = repository;
    }

    @Override
    public ValidationRecord save(ValidationRecord record) {
        ValidationRecordEntity entity = toEntity(record);
        repository.save(entity);
        return record;
    }

    @Override
    public List<ValidationRecord> findByTicketId(TicketId ticketId) {
        return repository.findByTicketId(ticketId.value())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private ValidationRecordEntity toEntity(ValidationRecord record) {
        return new ValidationRecordEntity(
                record.getId().value(),
                record.getTicketId().value(),
                record.getAgentId().value(),
                record.getValidatedAt(),
                record.getResult().name()
        );
    }

    private ValidationRecord toDomain(ValidationRecordEntity entity) {
        return new ValidationRecord(
                new ValidationRecordId(entity.getId()),
                new TicketId(entity.getTicketId()),
                new UserId(entity.getAgentId()),
                entity.getValidatedAt(),
                ValidationResult.valueOf(entity.getResult())
        );
    }
}
