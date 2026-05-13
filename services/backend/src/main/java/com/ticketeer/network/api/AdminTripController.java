package com.ticketeer.network.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/admin/trips")
public class AdminTripController {

    private final JdbcTemplate jdbcTemplate;

    public AdminTripController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PutMapping("/{id}")
    public AdminTripResponse updateTrip(
            @PathVariable UUID id,
            @Valid @RequestBody AdminTripRequest request
    ) {
        String sql = """
                UPDATE trips
                SET departure_station_code = ?,
                    arrival_station_code = ?,
                    departure_time = ?,
                    arrival_time = ?,
                    price = ?
                WHERE id = ?
                """;

        int updated = jdbcTemplate.update(
                sql,
                request.departureStationCode().trim().toUpperCase(),
                request.arrivalStationCode().trim().toUpperCase(),
                Timestamp.valueOf(request.departureTime()),
                Timestamp.valueOf(request.arrivalTime()),
                request.price(),
                id
        );

        if (updated == 0) {
            throw new IllegalArgumentException("Trajet introuvable : " + id);
        }

        return new AdminTripResponse(
                id,
                request.departureStationCode().trim().toUpperCase(),
                request.arrivalStationCode().trim().toUpperCase(),
                request.departureTime(),
                request.arrivalTime(),
                request.price()
        );
    }

    public record AdminTripRequest(
            @NotBlank
            String departureStationCode,

            @NotBlank
            String arrivalStationCode,

            @NotNull
            LocalDateTime departureTime,

            @NotNull
            LocalDateTime arrivalTime,

            @Positive
            double price
    ) {
    }

    public record AdminTripResponse(
            UUID id,
            String departureStationCode,
            String arrivalStationCode,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime,
            double price
    ) {
    }
}