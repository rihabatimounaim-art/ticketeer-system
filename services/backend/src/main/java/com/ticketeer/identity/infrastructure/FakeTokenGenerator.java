package com.ticketeer.identity.infrastructure;

import com.ticketeer.identity.application.port.TokenGenerator;
import com.ticketeer.identity.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class FakeTokenGenerator implements TokenGenerator {

    @Override
    public String generate(User user) {
        return "FAKE-TOKEN-" + user.getId().toString();
    }
}
