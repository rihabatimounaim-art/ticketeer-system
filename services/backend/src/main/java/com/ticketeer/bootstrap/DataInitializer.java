package com.ticketeer.bootstrap;

import com.ticketeer.identity.infrastructure.persistence.SpringDataUserRepository;
import com.ticketeer.identity.infrastructure.persistence.UserEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private final SpringDataUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(final SpringDataUserRepository userRepository,
                           final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(final String... args) {
        if (userRepository.findByEmail("admin@ticketeer.com").isEmpty()) {
            final UserEntity admin = new UserEntity(
                    UUID.fromString("11111111-1111-1111-1111-111111111111"),
                    "Admin",
                    "Ticketeer",
                    "admin@ticketeer.com",
                    passwordEncoder.encode("admin123"),
                    "ADMIN",
                    true
            );

            userRepository.save(admin);
        }
    }
}
