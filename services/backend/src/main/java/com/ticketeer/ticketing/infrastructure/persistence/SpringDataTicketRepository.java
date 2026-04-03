package com.ticketeer.ticketing.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataTicketRepository extends JpaRepository<TicketEntity, UUID> {
}
