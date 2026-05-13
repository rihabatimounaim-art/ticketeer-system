package com.ticketeer.bootstrap;

import com.ticketeer.identity.infrastructure.persistence.SpringDataUserRepository;
import com.ticketeer.identity.infrastructure.persistence.UserEntity;
import com.ticketeer.network.infrastructure.SpringDataStationRepository;
import com.ticketeer.network.infrastructure.SpringDataTripRepository;
import com.ticketeer.network.infrastructure.StationEntity;
import com.ticketeer.network.infrastructure.TripEntity;
import com.ticketeer.ticketing.infrastructure.persistence.SpringDataTicketRepository;
import com.ticketeer.ticketing.infrastructure.persistence.TicketEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private final SpringDataUserRepository userRepository;
    private final SpringDataStationRepository stationRepository;
    private final SpringDataTripRepository tripRepository;
    private final SpringDataTicketRepository ticketRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(final SpringDataUserRepository userRepository,
                           final SpringDataStationRepository stationRepository,
                           final SpringDataTripRepository tripRepository,
                           final SpringDataTicketRepository ticketRepository,
                           final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.stationRepository = stationRepository;
        this.tripRepository = tripRepository;
        this.ticketRepository = ticketRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(final String... args) {
        seedUsers();
        seedStations();
        seedTrips();
        seedPastTickets();
    }

    // ---- Users ----

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

    // ---- Stations ----

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

    // ---- Trips ----

    private void seedTrips() {
        if (tripRepository.count() > 0) {
            return;
        }

        final LocalDate d1 = LocalDate.now().plusDays(1);
        final LocalDate d2 = LocalDate.now().plusDays(2);
        final LocalDate d3 = LocalDate.now().plusDays(3);

        // Prochain samedi (discount weekend -10%)
        final LocalDate dSat = nextWeekday(DayOfWeek.SATURDAY, 1);

        // Samedi dans +15 jours minimum (discount weekend + anticipee -25%)
        final LocalDate dSatFar = nextWeekday(DayOfWeek.SATURDAY, 15);

        final List<TripEntity> trips = new ArrayList<>(List.of(
                // ---- Jour 1 (plein tarif) ----
                trip("PARIS", "LYON",        d1, 8,  0,  d1, 10, 0,  55.0),
                trip("PARIS", "LYON",        d1, 14, 0,  d1, 16, 0,  60.0),
                trip("LYON",  "MARSEILLE",   d1, 11, 0,  d1, 13, 0,  40.0),
                trip("PARIS", "LILLE",       d1, 9,  0,  d1, 10, 15, 35.0),
                trip("PARIS", "BORDEAUX",    d1, 7,  30, d1, 10, 30, 50.0),
                trip("PARIS", "NANTES",      d1, 12, 0,  d1, 14, 30, 45.0),
                trip("PARIS", "TOULOUSE",    d1, 6,  30, d1, 10, 0,  65.0),
                trip("PARIS", "STRASBOURG",  d1, 8,  0,  d1, 10, 45, 48.0),
                trip("LYON",  "NICE",        d1, 13, 0,  d1, 15, 30, 42.0),
                trip("PARIS", "RENNES",      d1, 10, 0,  d1, 12, 0,  38.0),
                // ---- Jour 2 (plein tarif) ----
                trip("BORDEAUX",   "NANTES",    d2, 15, 0,  d2, 17, 0,  30.0),
                trip("LYON",       "PARIS",     d2, 8,  0,  d2, 10, 0,  55.0),
                trip("MARSEILLE",  "LYON",      d2, 9,  0,  d2, 11, 0,  40.0),
                trip("TOULOUSE",   "PARIS",     d2, 7,  0,  d2, 10, 30, 65.0),
                trip("NICE",       "MARSEILLE", d2, 10, 0,  d2, 11, 30, 25.0),
                trip("STRASBOURG", "PARIS",     d2, 9,  0,  d2, 11, 45, 48.0),
                trip("RENNES",     "PARIS",     d2, 11, 0,  d2, 13, 0,  38.0),
                // ---- Jour 3 (plein tarif) ----
                trip("LILLE",      "PARIS",     d3, 18, 0,  d3, 19, 15, 35.0),
                trip("NANTES",     "BORDEAUX",  d3, 14, 0,  d3, 16, 0,  30.0),
                trip("MARSEILLE",  "NICE",      d3, 12, 0,  d3, 13, 30, 25.0),
                // ---- Prochain samedi — discount WEEKEND -10% ----
                trip("PARIS", "LYON",       dSat, 9,  0,  dSat, 11, 0,  55.0),
                trip("PARIS", "BORDEAUX",   dSat, 10, 0,  dSat, 13, 0,  50.0),
                trip("LYON",  "MARSEILLE",  dSat, 14, 0,  dSat, 16, 0,  40.0),
                // ---- Samedi lointain — discount WEEKEND + ANTICIPEE -25% ----
                trip("PARIS", "NICE",       dSatFar, 7,  0,  dSatFar, 11, 30, 80.0),
                trip("PARIS", "MARSEILLE",  dSatFar, 8,  0,  dSatFar, 11, 0,  75.0),
                trip("LYON",  "PARIS",      dSatFar, 12, 0,  dSatFar, 14, 0,  55.0)
        ));

        tripRepository.saveAll(trips);
    }

    /**
     * Returns the next occurrence of the given weekday that is at least minDaysAhead from today.
     */
    private LocalDate nextWeekday(DayOfWeek target, int minDaysAhead) {
        LocalDate d = LocalDate.now().plusDays(minDaysAhead);
        while (d.getDayOfWeek() != target) {
            d = d.plusDays(1);
        }
        return d;
    }

    // ---- Past tickets (for historique demo) ----

    private void seedPastTickets() {
        if (ticketRepository.count() > 0) {
            return;
        }

        final UUID miaId    = UUID.fromString("11111111-1111-1111-1111-111111111111");
        final UUID lolaId   = UUID.fromString("22222222-2222-2222-2222-222222222222");
        final UUID rihabeId = UUID.fromString("33333333-3333-3333-3333-333333333333");

        // Voyage il y a 30 jours (Paris → Lyon, Mia)
        pastTicket(miaId,    "PARIS", "LYON",       30, 8,  10, 55.0);
        // Voyage il y a 20 jours (Lyon → Marseille, Mia)
        pastTicket(miaId,    "LYON",  "MARSEILLE",  20, 11, 13, 40.0);
        // Voyage il y a 45 jours (Paris → Bordeaux, Lola)
        pastTicket(lolaId,   "PARIS", "BORDEAUX",   45, 7,  10, 50.0);
        // Voyage il y a 10 jours (Paris → Lille, Rihabe)
        pastTicket(rihabeId, "PARIS", "LILLE",      10, 9,  10, 35.0);
        // Voyage il y a 5 jours (Paris → Nantes, Rihabe)
        pastTicket(rihabeId, "PARIS", "NANTES",      5, 12, 14, 45.0);
    }

    private void pastTicket(UUID holderId, String from, String to,
                             int daysAgo, int depHour, int arrHour, double price) {
        final LocalDate travelDate = LocalDate.now().minusDays(daysAgo);
        final Instant dep        = travelDate.atTime(depHour, 0).toInstant(ZoneOffset.UTC);
        final Instant arr        = travelDate.atTime(arrHour, 0).toInstant(ZoneOffset.UTC);
        final Instant validFrom  = dep.minusSeconds(7200);   // dep - 2h
        final Instant validUntil = arr.plusSeconds(7200);    // arr + 2h
        final Instant issuedAt   = travelDate.minusDays(3).atTime(10, 0).toInstant(ZoneOffset.UTC);

        ticketRepository.save(new TicketEntity(
                UUID.randomUUID(), holderId,
                validFrom, validUntil,
                from, to, dep, arr,
                price, "VALID", issuedAt
        ));
    }

    // ---- helpers ----

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
