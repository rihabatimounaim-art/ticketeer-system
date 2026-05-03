package com.ticketeer.config;

import com.ticketeer.control.application.port.ValidationRepository;
import com.ticketeer.control.application.usecase.ValidateTicketUseCase;
import com.ticketeer.identity.application.port.PasswordVerifier;
import com.ticketeer.identity.application.port.TokenGenerator;
import com.ticketeer.identity.application.port.UserRepository;
import com.ticketeer.identity.application.usecase.AuthenticateUserUseCase;
import com.ticketeer.identity.infrastructure.JwtProperties;
import com.ticketeer.identity.infrastructure.JwtTokenGenerator;
import com.ticketeer.shared.domain.time.DomainClock;
import com.ticketeer.shared.infrastructure.SystemDomainClock;
import com.ticketeer.ticketing.application.port.PdfTicketGenerator;
import com.ticketeer.ticketing.application.port.QrCodeGenerator;
import com.ticketeer.ticketing.application.port.QrImageGenerator;
import com.ticketeer.ticketing.application.port.SignatureService;
import com.ticketeer.ticketing.application.port.TicketRepository;
import com.ticketeer.ticketing.application.usecase.GenerateTicketPdfUseCase;
import com.ticketeer.ticketing.application.usecase.GenerateTicketQrUseCase;
import com.ticketeer.ticketing.application.usecase.GetMyTicketsUseCase;
import com.ticketeer.ticketing.application.usecase.IssueTicketUseCase;
import com.ticketeer.ticketing.infrastructure.HmacSignatureService;
import com.ticketeer.ticketing.infrastructure.OpenPdfTicketGenerator;
import com.ticketeer.ticketing.infrastructure.TicketQrProperties;
import com.ticketeer.ticketing.infrastructure.ZxingQrImageGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.ticketeer.network.application.SearchTripsUseCase;
import com.ticketeer.network.infrastructure.SpringDataTripRepository;

@Configuration
public class AppConfig {

    @Bean
    public TicketQrProperties ticketQrProperties(
            @Value("${ticketing.qr.secret}") String secret
    ) {
        return new TicketQrProperties(secret);
    }

    @Bean
    @Primary
    public SignatureService signatureService(TicketQrProperties ticketQrProperties) {
        return new HmacSignatureService(ticketQrProperties);
    }

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

    @Bean
    public QrImageGenerator qrImageGenerator() {
        return new ZxingQrImageGenerator();
    }

    @Bean
    public PdfTicketGenerator pdfTicketGenerator() {
        return new OpenPdfTicketGenerator();
    }

    @Bean
    public GenerateTicketQrUseCase generateTicketQrUseCase(TicketRepository ticketRepository,
                                                           UserRepository userRepository,
                                                           SignatureService signatureService,
                                                           QrCodeGenerator qrCodeGenerator,
                                                           QrImageGenerator qrImageGenerator) {
        return new GenerateTicketQrUseCase(
                ticketRepository,
                userRepository,
                signatureService,
                qrCodeGenerator,
                qrImageGenerator
        );
    }

    @Bean
    public GenerateTicketPdfUseCase generateTicketPdfUseCase(TicketRepository ticketRepository,
                                                             UserRepository userRepository,
                                                             SignatureService signatureService,
                                                             QrCodeGenerator qrCodeGenerator,
                                                             QrImageGenerator qrImageGenerator,
                                                             PdfTicketGenerator pdfTicketGenerator) {
        return new GenerateTicketPdfUseCase(
                ticketRepository,
                userRepository,
                signatureService,
                qrCodeGenerator,
                qrImageGenerator,
                pdfTicketGenerator
        );
    }
    @Bean
    public SearchTripsUseCase searchTripsUseCase(SpringDataTripRepository tripRepository) {
        return new SearchTripsUseCase(tripRepository);
}
}
