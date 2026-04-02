package com.ticketeer.identity.application.port;

import com.ticketeer.identity.domain.model.User;

import java.util.Optional;

/**
 * Port for accessing user persistence.
 */
public interface UserRepository {

    Optional<User> findByEmail(String email);
}
