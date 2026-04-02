# Ticketeer Backend Domain Model v1

## Objective

Define the backend domain model before implementation, following a DDD-oriented and Clean Architecture-compatible structure.

---

## Bounded contexts

The backend is organized around the following bounded contexts:

- identity
- network
- tripsearch
- pricing
- booking
- ticketing
- control
- sync
- admin

Each context owns its business language, rules, and internal model.

---

## 1. Identity context

### Purpose

Manage authentication, authorization, and user identity.

### Main entities

#### User
- userId
- firstName
- lastName
- email
- phoneNumber
- passwordHash
- role
- passengerProfile
- identityDocumentPhotoRef
- active

### Value objects
- UserId
- EmailAddress
- PhoneNumber
- UserRole
- PassengerProfile

### Business rules
- a user has exactly one system role
- authentication is based on email and password
- only predefined users exist in the academic scope

---

## 2. Network context

### Purpose

Represent the railway reference network managed by the operator.

### Main entities

#### Station
- stationId
- code
- cityName
- displayName
- active

#### RailLine
- lineId
- code
- displayName
- active

#### RailService
Represents a scheduled train service for a given date and direction.

- serviceId
- lineId
- trainCode
- direction
- operatingDate
- active

#### ServiceStop
- serviceStopId
- serviceId
- stationId
- sequenceNumber
- arrivalTime
- departureTime

### Value objects
- StationId
- LineId
- ServiceId
- Direction
- StopTime

### Business rules
- the network is fixed and predefined
- stations and services are maintained by the operator
- correspondence points are limited to stations shared by services

---

## 3. TripSearch context

### Purpose

Produce journey proposals from origin, destination, and date.

### Main objects

#### JourneyProposal
Not necessarily persisted.

- proposalId
- originStationId
- destinationStationId
- operatingDate
- segments
- departureDateTime
- arrivalDateTime
- totalDuration
- estimatedPrice

#### JourneySegment
- serviceId
- originStationId
- destinationStationId
- departureDateTime
- arrivalDateTime
- segmentOrder

### Value objects
- ProposalId
- Duration
- JourneySegmentSnapshot

### Business rules
- only routes supported by the predefined network can be proposed
- proposals may contain one or more segments
- the search strategy can prioritize the fastest route in the nominal scenario

---

## 4. Pricing context

### Purpose

Calculate ticket price according to network data, seasonal coefficients, and customer discounts.

### Main entities

#### BaseFareRule
- fareRuleId
- originStationId
- destinationStationId
- baseAmount
- currency
- active

#### SeasonalPricingRule
- seasonalRuleId
- label
- coefficient
- startDate
- endDate
- active

#### PassengerDiscountRule
- discountRuleId
- passengerProfile
- percentage
- active

### Value objects
- Money
- Percentage
- CurrencyCode
- DateRange

### Business rules
- total fare is based on segment accumulation or station pair rules
- seasonal rules may adjust the price
- passenger discounts apply after base fare calculation according to project rules

---

## 5. Booking context

### Purpose

Confirm a journey selection and create a reservation record before ticket issuance.

### Main entities

#### Booking
- bookingId
- userId
- selectedJourneySnapshot
- bookingStatus
- paymentStatus
- bookedAt

### Value objects
- BookingId
- BookingStatus
- PaymentStatus
- JourneySnapshot

### Business rules
- a booking belongs to one authenticated user
- payment is simulated in the current project scope
- a booking is the source event for ticket issuance

---

## 6. Ticketing context

### Purpose

Issue and manage the electronic ticket as the central travel artifact.

### Main entities

#### Ticket
- ticketId
- bookingId
- holderUserId
- ticketNumber
- ticketStatus
- validFrom
- validUntil
- journeySnapshot
- qrPayloadVersion
- signatureDigest
- issuedAt

#### TicketDocument
- documentId
- ticketId
- documentFormat
- storageReference
- generatedAt

### Value objects
- TicketId
- TicketNumber
- TicketStatus
- ValidityWindow
- SignedQrPayload
- SignatureDigest
- DocumentFormat

### Business rules
- a ticket is nominative
- a ticket is linked to one traveler and one booked journey snapshot
- ticket authenticity relies on signed QR data
- the PDF is a representation of the ticket, not the source of truth

---

## 7. Control context

### Purpose

Validate a scanned ticket against authenticity, time validity, and travel coherence rules.

### Main entities

#### ValidationRecord
- validationId
- ticketId
- agentUserId
- scannedAtDevice
- receivedAtServer
- validationResult
- controlContext
- createdOnline

### Value objects
- ValidationId
- ValidationResult
- ControlContext
- ScanTimestamp

### Business rules
- a ticket may be valid and already controlled
- authenticity, temporal validity, and travel coherence must all be evaluated
- the control result must be explicit for the agent

---

## 8. Sync context

### Purpose

Manage delayed synchronization of offline validations from the agent device.

### Main entities

#### OfflineValidationBatch
- batchId
- deviceId
- submittedAt
- synchronizationStatus

#### PendingValidationEvent
- pendingEventId
- batchId
- ticketId
- agentUserId
- scannedAtDevice
- idempotencyKey
- payloadHash

### Value objects
- BatchId
- PendingEventId
- DeviceId
- IdempotencyKey
- SynchronizationStatus

### Business rules
- synchronization must be idempotent
- replaying the same offline validation must not duplicate persisted effects
- delayed validations remain traceable

---

## 9. Admin context

### Purpose

Maintain reference data and configurable business rules.

### Main entities

#### ManagedReferenceChange
- changeId
- changedByUserId
- targetContext
- targetEntityType
- targetEntityId
- changeType
- changedAt

### Business rules
- only admin users can modify reference data
- admin changes must remain auditable

---

## Cross-context relationships

### Key references
- Booking references User and JourneySnapshot
- Ticket references Booking and holder User
- ValidationRecord references Ticket and agent User
- JourneyProposal depends on Network and Pricing contexts
- Sync persists offline control events that eventually create ValidationRecord entries

### Ownership principle
- identity owns users and roles
- network owns stations, lines, services, and stops
- pricing owns fare and discount rules
- booking owns reservation lifecycle
- ticketing owns ticket lifecycle
- control owns validation history and decision outcomes
- sync owns offline replay and idempotency concerns

---

## Aggregates to implement first

Recommended initial aggregates:

1. User
2. Booking
3. Ticket
4. ValidationRecord
5. RailService

These are sufficient to bootstrap the first meaningful vertical slices.

---

## Domain services to plan

### Identity
- AuthenticationPolicy

### TripSearch
- RoutePlanner
- CorrespondenceResolver

### Pricing
- FareCalculator
- DiscountResolver

### Ticketing
- TicketFactory
- QrPayloadSigner
- ValidityWindowCalculator

### Control
- TicketAuthenticityVerifier
- TicketValidityPolicy
- RecontrolPolicy

### Sync
- ValidationReplayService
- IdempotencyService
- SyncConflictResolver

---

## Implementation guidance

### Inside each backend module
Use this internal structure:

```text
<module>/
├── domain/
│   ├── model/
│   ├── port/
│   ├── policy/
│   └── event/
├── application/
│   ├── usecase/
│   ├── command/
│   ├── query/
│   └── dto/
├── infrastructure/
│   ├── persistence/
│   ├── config/
│   └── external/
└── api/
    ├── rest/
    └── mapper/
```

### Rule
- no framework annotations in domain model
- no persistence concerns inside value objects or entities
- API DTOs must stay outside domain
- signed QR and PDF generation are infrastructure concerns attached to ticketing, not the core model
