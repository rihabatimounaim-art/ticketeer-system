package com.ticketeer.identity.application.command;

/**
 * Input command for the authentication use case.
 */
public record AuthenticateUserCommand(
        String email,
        String rawPassword
) {
}
