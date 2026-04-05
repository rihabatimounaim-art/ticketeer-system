package com.ticketeer.config;

import com.ticketeer.identity.infrastructure.JwtProperties;
import com.ticketeer.identity.infrastructure.JwtTokenGenerator;
import com.ticketeer.shared.infrastructure.SystemDomainClock;
import org.springframework.beans.factory.annotation.Value;
import com.ticketeer.identity.application.port.PasswordVerifier;
import com.ticketeer.identity.application.port.TokenGenerator;
import com.ticketeer.identity.application.port.UserRepository;
import com.ticketeer.identity.application.usecase.AuthenticateUserUseCase;
import com.ticketeer.shared.domain.time.DomainClock;
import com.ticketeer.ticketing.application.port.QrCodeGenerator;
import com.ticketeer.ticketing.application.port.SignatureService;
import com.ticketeer.ticketing.application.port.TicketRepository;
import com.ticketeer.ticketing.application.usecase.IssueTicketUseCase;
import com.ticketeer.control.application.port.ValidationRepository;
import com.ticketeer.control.application.usecase.ValidateTicketUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.ticketeer.ticketing.application.usecase.GetMyTicketsUseCase;

@Configuration
public class AppConfig {

    
    @Bean
    public AuthenticateUserUseCase authenticateUserUseCase(UserRepository userRepository,
                                                           PasswordVerifier passwordVerifier,
                                                           TokenGenerator tokenGenerator) {
        return new AuthenticateUserUseCase(userRepository, passwordVerifier, tokenGenerator);
    }

    @Bean
    public IssueTicketUseCase issueTicketUseCase(TicketRepository ticketRepository,
                                                 QrCodeGenerator qrCodeGenerator,
                                                 SignatureService signatureService,
                                                 DomainClock clock) {
        return new IssueTicketUseCase(ticketRepository, qrCodeGenerator, signatureService, clock);
    }
    
    @Bean
    public GetMyTicketsUseCase getMyTicketsUseCase(TicketRepository ticketRepository) {
         return new GetMyTicketsUseCase(ticketRepository);
    }
    @Bean
    public ValidateTicketUseCase validateTicketUseCase(TicketRepository ticketRepository,
                                                       ValidationRepository validationRepository,
                                                       DomainClock clock) {
        return new ValidateTicketUseCase(ticketRepository, validationRepository, clock);
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
