package com.ticketeer.identity.infrastructure;

import com.ticketeer.identity.application.port.UserRepository;
import com.ticketeer.identity.domain.model.User;
import com.ticketeer.identity.domain.model.UserId;
import com.ticketeer.identity.domain.model.UserRole;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> users = Map.of(
            "admin@ticketeer.com",
            new User(
                    new UserId(UUID.fromString("11111111-1111-1111-1111-111111111111")),
                    "Admin",
                    "User",
                    "admin@ticketeer.com",
                    "admin123",
                    UserRole.ADMIN,
                    true
            )
    );

    @Override
    public Optional<User> findByEmail(final String email) {
        return Optional.ofNullable(users.get(email));
    }

    @Override
    public Optional<User> findById(final UserId id) {
        return users.values().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }
}
