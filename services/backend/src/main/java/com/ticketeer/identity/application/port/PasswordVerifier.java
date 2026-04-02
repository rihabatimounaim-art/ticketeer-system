package com.ticketeer.identity.application.port;

/**
 * Port responsible for verifying passwords.
 */
public interface PasswordVerifier {

    boolean matches(String rawPassword, String storedHash);
}
