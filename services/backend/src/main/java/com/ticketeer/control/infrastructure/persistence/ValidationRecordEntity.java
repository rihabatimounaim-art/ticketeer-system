package com.ticketeer.control.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "validation_records")
public class ValidationRecordEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID ticketId;

    @Column(nullable = false)
    private UUID agentId;

    @Column(nullable = false)
    private Instant validatedAt;

    @Column(nullable = false)
    private String result;

    protected ValidationRecordEntity() {
    }

    public ValidationRecordEntity(UUID id,
                                  UUID ticketId,
                                  UUID agentId,
                                  Instant validatedAt,
                                  String result) {
        this.id = id;
        this.ticketId = ticketId;
        this.agentId = agentId;
        this.validatedAt = validatedAt;
        this.result = result;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public UUID getAgentId() {
        return agentId;
    }

    public Instant getValidatedAt() {
        return validatedAt;
    }

    public String getResult() {
        return result;
    }
}
