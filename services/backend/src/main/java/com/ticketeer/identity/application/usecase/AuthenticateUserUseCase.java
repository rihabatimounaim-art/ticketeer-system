package com.ticketeer.identity.application.usecase;

import com.ticketeer.identity.application.command.AuthenticateUserCommand;
import com.ticketeer.identity.application.port.PasswordVerifier;
import com.ticketeer.identity.application.port.TokenGenerator;
import com.ticketeer.identity.application.port.UserRepository;
import com.ticketeer.identity.domain.model.User;
import com.ticketeer.shared.domain.exception.BusinessRuleViolationException;

/**
 * Use case for authenticating a user.
 */
public class AuthenticateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordVerifier passwordVerifier;
    private final TokenGenerator tokenGenerator;

    public AuthenticateUserUseCase(
            final UserRepository userRepository,
            final PasswordVerifier passwordVerifier,
            final TokenGenerator tokenGenerator
    ) {
        this.userRepository = userRepository;
        this.passwordVerifier = passwordVerifier;
        this.tokenGenerator = tokenGenerator;
    }

    public String execute(final AuthenticateUserCommand command) {

        if (command.email() == null || command.email().isBlank()) {
            throw new BusinessRuleViolationException("Email must be provided");
        }
        if (command.rawPassword() == null || command.rawPassword().isBlank()) {
            throw new BusinessRuleViolationException("Password must be provided");
        }

        final User user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new BusinessRuleViolationException("Invalid credentials"));

        user.ensureActive();
        user.authenticate(passwordVerifier, command.rawPassword());

        return tokenGenerator.generate(user);
    }
}
