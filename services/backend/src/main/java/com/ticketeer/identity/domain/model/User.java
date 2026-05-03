package com.ticketeer.identity.domain.model;

import com.ticketeer.identity.application.port.PasswordVerifier;
import com.ticketeer.shared.domain.exception.BusinessRuleViolationException;

import java.util.Objects;

/**
 * User aggregate root.
 */
public class User {

    private final UserId id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String passwordHash;
    private final UserRole role;
    private final boolean active;

    public User(
            final UserId id,
            final String firstName,
            final String lastName,
            final String email,
            final String passwordHash,
            final UserRole role,
            final boolean active
    ) {
        if (id == null) throw new BusinessRuleViolationException("UserId is required");
        if (email == null || email.isBlank()) throw new BusinessRuleViolationException("Email is required");
        if (passwordHash == null || passwordHash.isBlank()) throw new BusinessRuleViolationException("Password hash is required");
        if (role == null) throw new BusinessRuleViolationException("Role is required");

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.active = active;
    }

    public UserId getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean isActive() {
        return active;
    }

    public void ensureActive() {
        if (!active) {
            throw new BusinessRuleViolationException("User is not active");
        }
    }

    public void authenticate(final PasswordVerifier passwordVerifier, final String rawPassword) {
    if (passwordVerifier == null) {
        throw new BusinessRuleViolationException("Password verifier is required");
    }
    if (rawPassword == null || rawPassword.isBlank()) {
        throw new BusinessRuleViolationException("Password must be provided");
    }
    if (!passwordVerifier.matches(rawPassword, this.passwordHash)) {
        throw new BusinessRuleViolationException("Invalid credentials");
    }
}
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
