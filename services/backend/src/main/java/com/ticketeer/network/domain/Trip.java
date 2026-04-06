package com.ticketeer.network.domain;

import java.time.Instant;
import java.util.UUID;

public class Trip {

    private final UUID id;
    private final String departureStationCode;
    private final String arrivalStationCode;
    private final Instant departureTime;
    private final Instant arrivalTime;
    private final double price;

    public Trip(final UUID id,
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
