# Shared Kernel v1

## Objective

Define the shared kernel before implementation.

The shared kernel contains only cross-cutting domain concepts that are:

- stable
- reused across multiple bounded contexts
- independent from frameworks and infrastructure

It must stay small.

---

## Scope

The shared kernel can contain:

- common base identifiers
- cross-context value objects
- common domain exceptions
- base domain events abstractions
- time abstractions
- money abstractions
- audit abstractions when truly shared

It must NOT contain:

- business logic specific to one bounded context
- repositories
- persistence annotations
- REST DTOs
- Spring-specific classes
- JPA entities

---

## Recommended package structure

```text
shared/
├── domain/
│   ├── model/
│   ├── exception/
│   ├── event/
│   ├── time/
│   └── validation/
├── application/
└── infrastructure/
```

---

## Core shared abstractions

## 1. Base identifiers

### Purpose
Provide a consistent identity model for entities and aggregates.

### Candidates
- AggregateId
- EntityId
- TechnicalId

### Rule
Every concrete identifier in bounded contexts should wrap a primitive value instead of exposing raw strings or UUIDs everywhere.

### Examples of concrete specializations
- UserId
- BookingId
- TicketId
- ValidationId
- StationId

The base abstraction may be shared, but the concrete identifier types remain inside their bounded contexts unless reused across multiple contexts.

---

## 2. Money

### Purpose
Represent monetary values without leaking primitive decimal usage into domain logic.

### Attributes
- amount
- currency

### Rules
- money is immutable
- arithmetic must preserve currency consistency
- comparisons must reject incompatible currencies
- formatting is NOT part of the domain object

### Why shared
Pricing, booking, and ticketing all depend on monetary calculations or representation.

---

## 3. Percentage

### Purpose
Represent discount rates and pricing coefficients safely.

### Attributes
- value

### Rules
- immutable
- range constraints enforced at construction
- explicit semantics for percentage versus multiplier must be preserved

### Why shared
Pricing rules and seasonal adjustments use it repeatedly.

---

## 4. DateRange

### Purpose
Represent validity windows and active periods.

### Attributes
- start
- end

### Rules
- immutable
- start must be before or equal to end
- must expose inclusion and overlap semantics

### Why shared
Used in ticket validity, seasonal pricing rules, and other temporal business checks.

---

## 5. Clock abstraction

### Purpose
Avoid hard dependency on system time inside business rules.

### Candidates
- DomainClock
- CurrentTimeProvider

### Rules
- domain logic must depend on an abstraction, not directly on system time
- infrastructure provides the concrete implementation

### Why shared
The functional documents explicitly identify time as an external actor in validity rules.

---

## 6. Domain exceptions

### Purpose
Provide a common error taxonomy for business rule violations.

### Candidates
- DomainException
- BusinessRuleViolationException
- NotFoundDomainException
- InvalidStateTransitionException
- ValidationFailureException

### Rules
- exceptions must express business meaning
- no HTTP semantics inside domain exceptions
- technical exceptions stay outside the domain layer

---

## 7. Domain events base abstractions

### Purpose
Support explicit business event modeling without infrastructure leakage.

### Candidates
- DomainEvent
- EventOccurredAt
- AggregateVersion

### Rules
- domain events are immutable
- they describe facts that already happened
- publication mechanisms belong outside the shared domain model

### Example future events
- BookingConfirmed
- TicketIssued
- TicketValidated
- OfflineValidationSynchronized

---

## 8. Validation primitives

### Purpose
Avoid copy-pasted low-level validation logic across modules.

### Candidates
- EmailAddress
- PhoneNumber
- NonBlankText
- CountryCode
- CurrencyCode

### Rule
Only include a value object in the shared kernel if it is reused by at least two bounded contexts or is foundational enough to justify standardization.

---

## Decision rules: what belongs in shared kernel?

A concept belongs in the shared kernel only if:

1. it is used by multiple bounded contexts
2. its meaning is stable across those contexts
3. centralizing it reduces duplication without coupling unrelated modules

If one of these conditions fails, keep the concept inside its own module.

---

## What must stay outside the shared kernel

### Keep inside `identity`
- User
- UserRole
- PassengerProfile
- authentication rules

### Keep inside `network`
- Station
- RailService
- Direction

### Keep inside `pricing`
- BaseFareRule
- SeasonalPricingRule
- PassengerDiscountRule

### Keep inside `ticketing`
- Ticket
- TicketStatus
- SignedQrPayload

### Keep inside `control`
- ValidationRecord
- ValidationResult
- ControlContext

These concepts are not generic enough and would pollute the shared kernel.

---

## Initial implementation shortlist

The shared kernel should start small.

Recommended first wave:

- DomainClock
- Money
- Percentage
- DateRange
- DomainException
- BusinessRuleViolationException
- ValidationFailureException

Optional second wave:

- EmailAddress
- PhoneNumber
- DomainEvent
- CurrencyCode

---

## Usage rules for implementation

### Rule 1
No Spring, no JPA, no framework annotation inside shared domain.

### Rule 2
All shared value objects must be immutable.

### Rule 3
The shared kernel must not become a dumping ground for utilities.

### Rule 4
Formatting, serialization, and persistence mapping belong outside the domain object.

### Rule 5
A shared abstraction must remain smaller than the cost of coupling it introduces.

---

## Recommended coding order after this document

1. DomainException hierarchy
2. DomainClock abstraction
3. Money
4. Percentage
5. DateRange
6. EmailAddress if needed immediately

This order minimizes refactoring in downstream modules.
