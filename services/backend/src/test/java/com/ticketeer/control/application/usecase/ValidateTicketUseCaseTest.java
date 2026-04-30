package com.ticketeer.control.application.usecase;

import com.ticketeer.control.application.port.ValidationRepository;
import com.ticketeer.control.domain.model.ValidationRecord;
import com.ticketeer.control.domain.model.ValidationResult;
import com.ticketeer.identity.domain.model.UserId;
import com.ticketeer.shared.domain.exception.BusinessRuleViolationException;
import com.ticketeer.shared.domain.model.DateRange;
import com.ticketeer.shared.domain.time.DomainClock;
import com.ticketeer.ticketing.application.port.TicketRepository;
import com.ticketeer.ticketing.domain.model.Ticket;
import com.ticketeer.ticketing.domain.model.TicketId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ValidateTicketUseCaseTest {

    private TicketRepository ticketRepository;
    private ValidationRepository validationRepository;
    private DomainClock clock;
    private ValidateTicketUseCase useCase;

    private final Instant now = Instant.parse("2026-05-01T10:00:00Z");
    private final TicketId ticketId = new TicketId(UUID.randomUUID());
    private final UserId agentId = new UserId(UUID.randomUUID());

    @BeforeEach
    void setUp() {
        ticketRepository = mock(TicketRepository.class);
        validationRepository = mock(ValidationRepository.class);
        clock = mock(DomainClock.class);
        when(clock.now()).thenReturn(now);
        useCase = new ValidateTicketUseCase(ticketRepository, validationRepository, clock);
    }

    @Test
    void should_return_valid_for_valid_ticket_not_yet_controlled() {
        final Ticket ticket = validTicket();
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(validationRepository.findByTicketId(ticketId)).thenReturn(List.of());
        when(validationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        final ValidationRecord record = useCase.execute(ticketId, agentId);

        assertThat(record.getResult()).isEqualTo(ValidationResult.VALID);
    }

    @Test
    void should_return_already_controlled_when_ticket_was_previously_validated() {
        final Ticket ticket = validTicket();
        final ValidationRecord previous = mock(ValidationRecord.class);
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(validationRepository.findByTicketId(ticketId)).thenReturn(List.of(previous));
        when(validationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        final ValidationRecord record = useCase.execute(ticketId, agentId);

        assertThat(record.getResult()).isEqualTo(ValidationResult.ALREADY_CONTROLLED);
    }

    @Test
    void should_return_expired_when_ticket_is_expired() {
        final Ticket expiredTicket = expiredTicket();
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(expiredTicket));
        when(validationRepository.findByTicketId(ticketId)).thenReturn(List.of());
        when(validationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        final ValidationRecord record = useCase.execute(ticketId, agentId);

        assertThat(record.getResult()).isEqualTo(ValidationResult.EXPIRED);
    }

    @Test
    void should_throw_when_ticket_not_found() {
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(ticketId, agentId))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("Ticket not found");
    }

    private Ticket validTicket() {
        Ticket ticket = new Ticket(
                ticketId,
                new UserId(UUID.randomUUID()),
                new DateRange(now.minusSeconds(3600), now.plusSeconds(3600)),
                "PARIS", "LYON",
                now.minusSeconds(7200), now.minusSeconds(3600),
                55.0, now.minusSeconds(86400)
        );
        ticket.activate();
        return ticket;
    }

    private Ticket expiredTicket() {
        return Ticket.rehydrate(
                ticketId,
                new UserId(UUID.randomUUID()),
                new DateRange(now.minusSeconds(7200), now.minusSeconds(3600)),
                "PARIS", "LYON",
                now.minusSeconds(10800), now.minusSeconds(7200),
                55.0, now.minusSeconds(86400),
                com.ticketeer.ticketing.domain.model.TicketStatus.VALID
        );
    }
}
