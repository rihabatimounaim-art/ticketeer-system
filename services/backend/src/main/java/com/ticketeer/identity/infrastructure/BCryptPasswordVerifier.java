package com.ticketeer.identity.infrastructure;

import com.ticketeer.identity.application.port.PasswordVerifier;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Primary
public class BCryptPasswordVerifier implements PasswordVerifier {

    private final PasswordEncoder passwordEncoder;

    public BCryptPasswordVerifier(final PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean matches(final String rawPassword, final String storedHash) {
        return passwordEncoder.matches(rawPassword, storedHash);
    }
}
