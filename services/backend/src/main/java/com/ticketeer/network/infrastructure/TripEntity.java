package com.ticketeer.network.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "trips")
public class TripEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String departureStationCode;

    @Column(nullable = false)
    private String arrivalStationCode;

    @Column(nullable = false)
    private Instant departureTime;

    @Column(nullable = false)
    private Instant arrivalTime;

    @Column(nullable = false)
    private double price;

    protected TripEntity() {
    }

    public TripEntity(final UUID id,
                      final String departureStationCode,
                      final String arrivalStationCode,
                      final Instant departureTime,
                      final Instant arrivalTime,
                      final double price) {
        this.id = id;
        this.departureStationCode = departureStationCode;
        this.arrivalStationCode = arrivalStationCode;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
    }

    public UUID getId() {
        return id;
    }

    public String getDepartureStationCode() {
        return departureStationCode;
    }

    public String getArrivalStationCode() {
        return arrivalStationCode;
    }

    public Instant getDepartureTime() {
        return departureTime;
    }

    public Instant getArrivalTime() {
        return arrivalTime;
    }

    public double getPrice() {
        return price;
    }
}
