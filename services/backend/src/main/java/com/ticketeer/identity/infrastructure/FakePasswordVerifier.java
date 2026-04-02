package com.ticketeer.identity.infrastructure;

import com.ticketeer.identity.application.port.PasswordVerifier;
import org.springframework.stereotype.Component;

@Component
public class FakePasswordVerifier implements PasswordVerifier {

    @Override
    public boolean matches(String rawPassword, String storedHash) {
        return rawPassword.equals(storedHash);
    }
}
