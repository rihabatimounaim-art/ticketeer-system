package com.ticketeer.identity.api.rest;

import com.ticketeer.identity.application.command.AuthenticateUserCommand;
import com.ticketeer.identity.application.usecase.AuthenticateUserUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for authentication.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;

    public AuthController(final AuthenticateUserUseCase authenticateUserUseCase) {
        this.authenticateUserUseCase = authenticateUserUseCase;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody final LoginRequest request) {

        final String token = authenticateUserUseCase.execute(
                new AuthenticateUserCommand(
                        request.email(),
                        request.password()
                )
        );

        return new LoginResponse(token);
    }
}
