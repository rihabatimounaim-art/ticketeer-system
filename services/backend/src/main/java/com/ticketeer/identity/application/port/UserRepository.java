package com.ticketeer.identity.application.port;

import com.ticketeer.identity.domain.model.User;
import com.ticketeer.identity.domain.model.UserId;

import java.util.Optional;

/**
 * Port for accessing user persistence.
 */
public interface UserRepository {

    Optional<User> findByEmail(String email);

    Optional<User> findById(UserId id);
}
