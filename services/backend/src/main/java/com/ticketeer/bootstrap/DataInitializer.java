package com.ticketeer.bootstrap;

import com.ticketeer.identity.infrastructure.persistence.SpringDataUserRepository;
import com.ticketeer.identity.infrastructure.persistence.UserEntity;
import com.ticketeer.network.infrastructure.SpringDataStationRepository;
import com.ticketeer.network.infrastructure.SpringDataTripRepository;
import com.ticketeer.network.infrastructure.StationEntity;
import com.ticketeer.network.infrastructure.TripEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private final SpringDataUserRepository userRepository;
    private final SpringDataStationRepository stationRepository;
    private final SpringDataTripRepository tripRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(final SpringDataUserRepository userRepository,
                           final SpringDataStationRepository stationRepository,
                           final SpringDataTripRepository tripRepository,
                           final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.stationRepository = stationRepository;
        this.tripRepository = tripRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(final String... args) {
        seedUsers();
        seedStations();
        seedTrips();
    }

    private void seedUsers() {
        seedUser(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "Mia",
                "Bennani",
                "mia@ticketeer.com",
                "user123",
                "CUSTOMER"
        );

        seedUser(
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                "Lola",
                "Alaoui",
                "lola@ticketeer.com",
                "user123",
                "CUSTOMER"
        );

        seedUser(
                UUID.fromString("33333333-3333-3333-3333-333333333333"),
                "Rihabe",
                "Atimounaim",
                "rihabe@ticketeer.com",
                "user123",
                "CUSTOMER"
        );

        seedUser(
                UUID.fromString("44444444-4444-4444-4444-444444444444"),
                "Monir",
                "Controller",
                "monir@ticketeer.com",
                "control123",
                "AGENT"
        );

        seedUser(
                UUID.fromString("55555555-5555-5555-5555-555555555555"),
                "Ouail",
                "Admin",
                "ouail@ticketeer.com",
                "admin123",
                "ADMIN"
        );
    }

    private void seedUser(final UUID id,
                          final String firstName,
                          final String lastName,
                          final String email,
                          final String rawPassword,
                          final String role) {
        if (userRepository.findByEmail(email).isPresent()) {
            return;
        }

        userRepository.save(new UserEntity(
                id,
                firstName,
                lastName,
                email,
                passwordEncoder.encode(rawPassword),
                role,
                true
        ));
    }

    private void seedStations() {
        if (stationRepository.count() > 0) {
            return;
        }

        stationRepository.saveAll(List.of(
                new StationEntity("PARIS", "Paris"),
                new StationEntity("LYON", "Lyon"),
                new StationEntity("MARSEILLE", "Marseille"),
                new StationEntity("LILLE", "Lille"),
                new StationEntity("BORDEAUX", "Bordeaux"),
                new StationEntity("NANTES", "Nantes")
        ));
    }

    private void seedTrips() {
        if (tripRepository.count() > 0) {
            return;
        }

        tripRepository.saveAll(List.of(
                new TripEntity(UUID.randomUUID(), "PARIS", "LYON",
                        Instant.parse("2026-04-06T08:00:00Z"),
                        Instant.parse("2026-04-06T10:00:00Z"), 55.0),

                new TripEntity(UUID.randomUUID(), "PARIS", "LYON",
                        Instant.parse("2026-04-06T14:00:00Z"),
                        Instant.parse("2026-04-06T16:00:00Z"), 60.0),

                new TripEntity(UUID.randomUUID(), "LYON", "MARSEILLE",
                        Instant.parse("2026-04-06T11:00:00Z"),
                        Instant.parse("2026-04-06T13:00:00Z"), 40.0),

                new TripEntity(UUID.randomUUID(), "PARIS", "LILLE",
                        Instant.parse("2026-04-06T09:00:00Z"),
                        Instant.parse("2026-04-06T10:15:00Z"), 35.0),

                new TripEntity(UUID.randomUUID(), "PARIS", "BORDEAUX",
                        Instant.parse("2026-04-06T07:30:00Z"),
                        Instant.parse("2026-04-06T10:30:00Z"), 50.0),

                new TripEntity(UUID.randomUUID(), "PARIS", "NANTES",
                        Instant.parse("2026-04-06T12:00:00Z"),
                        Instant.parse("2026-04-06T14:30:00Z"), 45.0),

                new TripEntity(UUID.randomUUID(), "BORDEAUX", "NANTES",
                        Instant.parse("2026-04-06T15:00:00Z"),
                        Instant.parse("2026-04-06T17:00:00Z"), 30.0),

                new TripEntity(UUID.randomUUID(), "LYON", "PARIS",
                        Instant.parse("2026-04-07T08:00:00Z"),
                        Instant.parse("2026-04-07T10:00:00Z"), 55.0),

                new TripEntity(UUID.randomUUID(), "MARSEILLE", "LYON",
                        Instant.parse("2026-04-07T09:00:00Z"),
                        Instant.parse("2026-04-07T11:00:00Z"), 40.0),

                new TripEntity(UUID.randomUUID(), "LILLE", "PARIS",
                        Instant.parse("2026-04-07T18:00:00Z"),
                        Instant.parse("2026-04-07T19:15:00Z"), 35.0)
        ));
    }
}
