package com.ticketeer.identity.application.usecase;

import com.ticketeer.identity.application.command.AuthenticateUserCommand;
import com.ticketeer.identity.application.port.PasswordVerifier;
import com.ticketeer.identity.application.port.TokenGenerator;
import com.ticketeer.identity.application.port.UserRepository;
import com.ticketeer.identity.domain.model.User;
import com.ticketeer.identity.domain.model.UserId;
import com.ticketeer.identity.domain.model.UserRole;
import com.ticketeer.shared.domain.exception.BusinessRuleViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AuthenticateUserUseCaseTest {

    private UserRepository userRepository;
    private PasswordVerifier passwordVerifier;
    private TokenGenerator tokenGenerator;
    private AuthenticateUserUseCase useCase;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordVerifier = mock(PasswordVerifier.class);
        tokenGenerator = mock(TokenGenerator.class);
        useCase = new AuthenticateUserUseCase(userRepository, passwordVerifier, tokenGenerator);
    }

    @Test
    void should_return_token_when_credentials_are_valid() {
        final User user = new User(
                new UserId(UUID.randomUUID()), "Mia", "Bennani",
                "mia@ticketeer.com", "hashed", UserRole.CUSTOMER, true
        );
        when(userRepository.findByEmail("mia@ticketeer.com")).thenReturn(Optional.of(user));
        when(passwordVerifier.matches("password123", "hashed")).thenReturn(true);
        when(tokenGenerator.generate(user)).thenReturn("jwt.token.here");

        final String token = useCase.execute(new AuthenticateUserCommand("mia@ticketeer.com", "password123"));

        assertThat(token).isEqualTo("jwt.token.here");
    }

    @Test
    void should_throw_when_email_is_blank() {
        assertThatThrownBy(() -> useCase.execute(new AuthenticateUserCommand("", "password")))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("Email");
    }

    @Test
    void should_throw_when_password_is_blank() {
        assertThatThrownBy(() -> useCase.execute(new AuthenticateUserCommand("mia@ticketeer.com", "")))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("Password");
    }

    @Test
    void should_throw_when_user_not_found() {
        when(userRepository.findByEmail("unknown@ticketeer.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new AuthenticateUserCommand("unknown@ticketeer.com", "pass")))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("Invalid credentials");
    }

    @Test
    void should_throw_when_user_is_inactive() {
        final User inactive = new User(
                new UserId(UUID.randomUUID()), "Mia", "Bennani",
                "mia@ticketeer.com", "hashed", UserRole.CUSTOMER, false
        );
        when(userRepository.findByEmail("mia@ticketeer.com")).thenReturn(Optional.of(inactive));

        assertThatThrownBy(() -> useCase.execute(new AuthenticateUserCommand("mia@ticketeer.com", "password")))
                .isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    void should_throw_when_password_wrong() {
        final User user = new User(
                new UserId(UUID.randomUUID()), "Mia", "Bennani",
                "mia@ticketeer.com", "hashed", UserRole.CUSTOMER, true
        );
        when(userRepository.findByEmail("mia@ticketeer.com")).thenReturn(Optional.of(user));
        when(passwordVerifier.matches("wrong", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(new AuthenticateUserCommand("mia@ticketeer.com", "wrong")))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("Invalid credentials");
    }
}
