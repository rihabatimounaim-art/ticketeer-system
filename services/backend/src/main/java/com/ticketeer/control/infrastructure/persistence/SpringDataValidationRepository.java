package com.ticketeer.control.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataValidationRepository extends JpaRepository<ValidationRecordEntity, UUID> {

    List<ValidationRecordEntity> findByTicketId(UUID ticketId);
}
