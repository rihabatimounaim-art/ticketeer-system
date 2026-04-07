@PostMapping
public CreateTicketResponse createTicket(@RequestBody final CreateTicketRequest request) {

    final Ticket ticket = issueTicketUseCase.execute(
            new IssueTicketCommand(
                    new UserId(UUID.fromString(request.holderId())),
                    new DateRange(
                            Instant.parse(request.validFrom()),
                            Instant.parse(request.validUntil())
                    ),
                    request.departureStationCode(),
                    request.arrivalStationCode(),
                    Instant.parse(request.departureTime()),
                    Instant.parse(request.arrivalTime()),
                    request.price()
            )
    );

    return new CreateTicketResponse(
            ticket.getId().toString(),
            ticket.getStatus().name()
    );
}

@GetMapping("/me")
public List<MyTicketResponse> getMyTickets(@AuthenticationPrincipal final JwtAuthenticatedUser authenticatedUser) {
    final UserId holderId = new UserId(UUID.fromString(authenticatedUser.userId()));

    return getMyTicketsUseCase.execute(holderId)
            .stream()
            .map(ticket -> new MyTicketResponse(
                    ticket.getId().toString(),
                    ticket.getHolderId().toString(),
                    ticket.getDepartureStationCode(),
                    ticket.getArrivalStationCode(),
                    ticket.getDepartureTime().toString(),
                    ticket.getArrivalTime().toString(),
                    ticket.getPrice(),
                    ticket.getValidityWindow().getStart().toString(),
                    ticket.getValidityWindow().getEnd().toString(),
                    ticket.getStatus().name(),
                    ticket.getIssuedAt().toString()
            ))
            .toList();
}
