package com.ticketeer.network.domain;

import java.util.Objects;

public class Station {

    private final String code;
    private final String name;

    public Station(final String code, final String name) {
        this.code = Objects.requireNonNull(code);
        this.name = Objects.requireNonNull(name);
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
