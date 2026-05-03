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

import java.time.LocalDate;
import java.time.ZoneOffset;
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
        seedUser(UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "Mia", "Bennani", "mia@ticketeer.com", "user123", "CUSTOMER");
        seedUser(UUID.fromString("22222222-2222-2222-2222-222222222222"),
                "Lola", "Alaoui", "lola@ticketeer.com", "user123", "CUSTOMER");
        seedUser(UUID.fromString("33333333-3333-3333-3333-333333333333"),
                "Rihabe", "Atimounaim", "rihabe@ticketeer.com", "user123", "CUSTOMER");
        seedUser(UUID.fromString("44444444-4444-4444-4444-444444444444"),
                "Monir", "Controller", "monir@ticketeer.com", "control123", "AGENT");
        seedUser(UUID.fromString("55555555-5555-5555-5555-555555555555"),
                "Ouail", "Admin", "ouail@ticketeer.com", "admin123", "ADMIN");
    }

    private void seedUser(final UUID id, final String firstName, final String lastName,
                          final String email, final String rawPassword, final String role) {
        if (userRepository.findByEmail(email).isPresent()) {
            return;
        }
        userRepository.save(new UserEntity(id, firstName, lastName, email,
                passwordEncoder.encode(rawPassword), role, true));
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
                new StationEntity("NANTES", "Nantes"),
                new StationEntity("TOULOUSE", "Toulouse"),
                new StationEntity("STRASBOURG", "Strasbourg"),
                new StationEntity("NICE", "Nice"),
                new StationEntity("RENNES", "Rennes")
        ));
    }

    private void seedTrips() {
        if (tripRepository.count() > 0) {
            return;
        }

        // Generate trips relative to today so dates are always in the future
        final LocalDate d1 = LocalDate.now().plusDays(1);
        final LocalDate d2 = LocalDate.now().plusDays(2);
        final LocalDate d3 = LocalDate.now().plusDays(3);

        tripRepository.saveAll(List.of(
                // Jour 1
                trip("PARIS", "LYON", d1, 8, 0, d1, 10, 0, 55.0),
                trip("PARIS", "LYON", d1, 14, 0, d1, 16, 0, 60.0),
                trip("LYON", "MARSEILLE", d1, 11, 0, d1, 13, 0, 40.0),
                trip("PARIS", "LILLE", d1, 9, 0, d1, 10, 15, 35.0),
                trip("PARIS", "BORDEAUX", d1, 7, 30, d1, 10, 30, 50.0),
                trip("PARIS", "NANTES", d1, 12, 0, d1, 14, 30, 45.0),
                trip("PARIS", "TOULOUSE", d1, 6, 30, d1, 10, 0, 65.0),
                trip("PARIS", "STRASBOURG", d1, 8, 0, d1, 10, 45, 48.0),
                trip("LYON", "NICE", d1, 13, 0, d1, 15, 30, 42.0),
                trip("PARIS", "RENNES", d1, 10, 0, d1, 12, 0, 38.0),
                // Jour 2
                trip("BORDEAUX", "NANTES", d2, 15, 0, d2, 17, 0, 30.0),
                trip("LYON", "PARIS", d2, 8, 0, d2, 10, 0, 55.0),
                trip("MARSEILLE", "LYON", d2, 9, 0, d2, 11, 0, 40.0),
                trip("TOULOUSE", "PARIS", d2, 7, 0, d2, 10, 30, 65.0),
                trip("NICE", "MARSEILLE", d2, 10, 0, d2, 11, 30, 25.0),
                trip("STRASBOURG", "PARIS", d2, 9, 0, d2, 11, 45, 48.0),
                trip("RENNES", "PARIS", d2, 11, 0, d2, 13, 0, 38.0),
                // Jour 3
                trip("LILLE", "PARIS", d3, 18, 0, d3, 19, 15, 35.0),
                trip("NANTES", "BORDEAUX", d3, 14, 0, d3, 16, 0, 30.0),
                trip("MARSEILLE", "NICE", d3, 12, 0, d3, 13, 30, 25.0)
        ));
    }

    private TripEntity trip(String from, String to, LocalDate date,
                            int depH, int depM, LocalDate arrDate, int arrH, int arrM,
                            double price) {
        return new TripEntity(
                UUID.randomUUID(),
                from, to,
                date.atTime(depH, depM).toInstant(ZoneOffset.UTC),
                arrDate.atTime(arrH, arrM).toInstant(ZoneOffset.UTC),
                price
        );
    }
}
