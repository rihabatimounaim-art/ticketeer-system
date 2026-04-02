package com.ticketeer.identity.application.port;

import com.ticketeer.identity.domain.model.User;

/**
 * Port responsible for generating authentication tokens (e.g., JWT).
 */
public interface TokenGenerator {

    String generate(User user);
}
