package com.ticketeer.ticketing.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tickets")
public class TicketEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID holderId;

    @Column(nullable = false)
    private Instant validFrom;

    @Column(nullable = false)
    private Instant validUntil;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Instant issuedAt;

    protected TicketEntity() {
    }

    public TicketEntity(UUID id,
                        UUID holderId,
                        Instant validFrom,
                        Instant validUntil,
                        String status,
                        Instant issuedAt) {
        this.id = id;
        this.holderId = holderId;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.status = status;
        this.issuedAt = issuedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getHolderId() {
        return holderId;
    }

    public Instant getValidFrom() {
        return validFrom;
    }

    public Instant getValidUntil() {
        return validUntil;
    }

    public String getStatus() {
        return status;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }
}
