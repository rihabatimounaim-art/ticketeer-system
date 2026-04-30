package com.ticketeer.identity.api.rest;

import com.ticketeer.identity.application.command.AuthenticateUserCommand;
import com.ticketeer.identity.application.usecase.AuthenticateUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "User login and token management")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;

    public AuthController(final AuthenticateUserUseCase authenticateUserUseCase) {
        this.authenticateUserUseCase = authenticateUserUseCase;
    }

    @Operation(summary = "Login", description = "Authenticate with email and password, returns a JWT token")
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody final LoginRequest request) {
        final String token = authenticateUserUseCase.execute(
                new AuthenticateUserCommand(request.email(), request.password())
        );
        return new LoginResponse(token);
    }
}
