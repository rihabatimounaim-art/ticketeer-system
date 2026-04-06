package com.ticketeer.network.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataStationRepository extends JpaRepository<StationEntity, String> {
}
