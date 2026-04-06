package com.ticketeer.network.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "stations")
public class StationEntity {

    @Id
    private String code;

    @Column(nullable = false, unique = true)
    private String name;

    protected StationEntity() {
    }

    public StationEntity(final String code, final String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
