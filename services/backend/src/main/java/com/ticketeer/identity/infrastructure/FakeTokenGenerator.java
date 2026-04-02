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
    @Bean
public DomainClock domainClock() {
    return new SystemDomainClock();
}

@Bean
public JwtProperties jwtProperties(
        @Value("${security.jwt.secret}") String secret,
        @Value("${security.jwt.expiration-seconds}") long expirationSeconds
) {
    return new JwtProperties(secret, expirationSeconds);
}

@Bean
public TokenGenerator tokenGenerator(JwtProperties jwtProperties) {
    return new JwtTokenGenerator(jwtProperties);
}
}
