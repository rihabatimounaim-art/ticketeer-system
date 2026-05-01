# Ticketeer — Railway Ticketing System

> Plateforme complète de billetterie ferroviaire : achat de billets, génération QR/PDF, validation en temps réel et tableau de bord administrateur.

---

## Table des matières

1. [Vue d'ensemble](#vue-densemble)
2. [Architecture](#architecture)
3. [Structure du projet](#structure-du-projet)
4. [Stack technique](#stack-technique)
5. [Démarrage rapide](#démarrage-rapide)
   - [Prérequis](#prérequis)
   - [Mode développement (H2 in-memory)](#mode-développement-h2-in-memory)
   - [Mode production (PostgreSQL)](#mode-production-postgresql)
   - [Tout démarrer avec Docker](#tout-démarrer-avec-docker)
6. [Client web](#client-web)
7. [API REST](#api-rest)
   - [Authentification](#authentification)
   - [Trajets](#trajets)
   - [Billets](#billets)
   - [Contrôle](#contrôle)
8. [Comptes de démonstration](#comptes-de-démonstration)
9. [Base de données](#base-de-données)
10. [Sécurité](#sécurité)
11. [Tests](#tests)
12. [Variables d'environnement](#variables-denvironnement)
13. [Roadmap](#roadmap)

---

## Vue d'ensemble

Ticketeer est un système de billetterie ferroviaire complet conçu selon les principes du **Clean Architecture** et du **Domain-Driven Design**. Il couvre l'ensemble du cycle de vie d'un billet : recherche de trajet → achat → téléchargement QR/PDF → validation par un agent.

```
┌─────────────────────────────────────────────────────────────┐
│                       CLIENT WEB (SPA)                      │
│   Login  │  Espace Client  │  Agent Contrôle  │  Admin      │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTP / REST + JWT
┌────────────────────────▼────────────────────────────────────┐
│                    BACKEND API (Spring Boot 3)               │
│  identity │ network │ ticketing │ control │ shared/security  │
└────────────────────────┬────────────────────────────────────┘
                         │ JPA / Flyway
┌────────────────────────▼────────────────────────────────────┐
│            PostgreSQL 16  (H2 in-memory en dev)             │
└─────────────────────────────────────────────────────────────┘
```

---

## Architecture

Le backend est un **monolithe modulaire** organisé en contextes bornés (bounded contexts) indépendants. Chaque contexte suit le pattern Ports & Adapters (Hexagonale) :

```
domain/          ← entités, value objects, règles métier pures
application/     ← use cases, ports (interfaces)
infrastructure/  ← adapters (JPA, JWT, QR, PDF…)
api/rest/        ← controllers, DTOs (entrée/sortie)
```

### Contextes bornés

| Contexte | Responsabilité |
|----------|----------------|
| **identity** | Authentification JWT, gestion des utilisateurs, BCrypt |
| **network** | Gares, trajets, recherche par date/route |
| **ticketing** | Émission de billets, génération QR (ZXing), PDF (OpenPDF) |
| **control** | Validation de billets par les agents, audit log |
| **shared** | Exceptions domaine, `DateRange`, `DomainClock`, `GlobalExceptionHandler` |
| **security** | Filtre JWT Spring Security, configuration CORS |

---

## Structure du projet

```
ticketeer-system/
├── apps/
│   ├── web-client/               # SPA Vanilla JS — 4 pages
│   │   ├── index.html            # Page de connexion
│   │   ├── customer.html         # Espace client (recherche + billets)
│   │   ├── agent.html            # Interface de contrôle (scanner QR)
│   │   ├── admin.html            # Tableau de bord admin
│   │   ├── css/style.css         # Design system complet
│   │   └── js/api.js             # Client HTTP + utilitaires
│   └── agent-mobile/             # App mobile agent (scaffold)
├── services/
│   └── backend/                  # API Spring Boot 3
│       ├── src/main/java/com/ticketeer/
│       │   ├── identity/
│       │   ├── network/
│       │   ├── ticketing/
│       │   ├── control/
│       │   ├── shared/
│       │   ├── security/
│       │   ├── bootstrap/        # Seed data au démarrage
│       │   └── config/
│       ├── src/main/resources/
│       │   ├── application.yml
│       │   ├── application-h2.yml
│       │   ├── application-postgres.yml
│       │   └── db/migration/
│       │       └── V1__init_schema.sql
│       ├── src/test/             # Tests unitaires JUnit 5
│       ├── Dockerfile            # Multi-stage build
│       └── pom.xml
├── docs/
│   ├── adr/                      # Architecture Decision Records
│   ├── architecture/
│   └── domain/
│       ├── backend-domain-model.md
│       └── shared-kernel.md
├── docker-compose.yml            # PostgreSQL 16 + Backend
└── README.md
```

---

## Stack technique

### Backend

| Technologie | Version | Usage |
|-------------|---------|-------|
| Java | 17 | Langage |
| Spring Boot | 3.3.4 | Framework applicatif |
| Spring Security | 6.x | Authentification & autorisation |
| JJWT | 0.12.6 | Génération et validation JWT |
| Spring Data JPA | 3.x | Persistance |
| Flyway | 10.x | Migrations de schéma |
| PostgreSQL | 16 | Base de données production |
| H2 | 2.x | Base de données développement (in-memory) |
| ZXing | 3.5.3 | Génération de QR codes |
| OpenPDF | 1.3.39 | Génération de PDFs |
| Springdoc OpenAPI | 2.6.0 | Documentation Swagger UI |
| JUnit 5 + Mockito | — | Tests unitaires |

### Frontend

| Technologie | Usage |
|-------------|-------|
| HTML5 / CSS3 / Vanilla JS | SPA sans framework |
| html5-qrcode (CDN) | Scanner QR caméra (page agent) |
| Fetch API | Appels HTTP vers le backend |

### Infrastructure

| Technologie | Usage |
|-------------|-------|
| Docker | Conteneurisation backend |
| Docker Compose | Orchestration locale (DB + API) |
| eclipse-temurin:17 | Image de base JDK/JRE |

---

## Démarrage rapide

### Prérequis

- Java 17+
- Maven 3.8+
- Docker & Docker Compose (optionnel)
- Python 3 (pour servir le client web en local)

---

### Mode développement (H2 in-memory)

Le mode le plus simple — aucune base de données à installer.

```bash
cd services/backend
mvn spring-boot:run
```

L'API démarre sur **http://localhost:8080**.

La base H2 est initialisée en mémoire via Flyway au démarrage. Les données de démonstration (utilisateurs, gares, trajets) sont seedées automatiquement par `DataInitializer`.

> Console H2 disponible sur : http://localhost:8080/h2-console  
> JDBC URL : `jdbc:h2:mem:ticketeerdb`

**Servir le client web :**

```bash
cd apps/web-client
python3 -m http.server 8001
# Ouvrir : http://localhost:8001/index.html
```

---

### Mode production (PostgreSQL)

```bash
# 1. Démarrer PostgreSQL
docker compose up postgres -d

# 2. Lancer le backend avec le profil postgres
cd services/backend
SPRING_PROFILES_ACTIVE=postgres \
DATABASE_URL=jdbc:postgresql://localhost:5432/ticketeerdb \
DATABASE_USER=ticketeer \
DATABASE_PASSWORD=ticketeer \
mvn spring-boot:run
```

---

### Tout démarrer avec Docker

```bash
# Build et démarrage de l'ensemble (PostgreSQL + Backend)
docker compose up --build

# Arrêter
docker compose down

# Arrêter et supprimer les volumes (reset complet de la DB)
docker compose down -v
```

L'API est accessible sur **http://localhost:8080** après le healthcheck PostgreSQL (~10 secondes).

---

## Client web

Le client web est une SPA Vanilla JS organisée en 4 pages, chacune dédiée à un rôle :

| Page | URL | Rôle requis | Fonctionnalités |
|------|-----|-------------|-----------------|
| `index.html` | `/index.html` | Public | Connexion, redirection par rôle |
| `customer.html` | `/customer.html` | CLIENT, ADMIN | Recherche de trajet, achat de billet, consultation des billets, téléchargement QR/PDF |
| `agent.html` | `/agent.html` | AGENT, ADMIN | Saisie manuelle d'ID ou scan QR caméra, résultat de validation, historique de session |
| `admin.html` | `/admin.html` | ADMIN | Statistiques, liste des utilisateurs, gestion des gares, Swagger UI intégré |

**Authentification :** JWT stocké en `localStorage`. Chaque requête HTTP inclut automatiquement `Authorization: Bearer <token>`. La redirection vers `index.html` est déclenchée si le token est absent ou expiré.

---

## API REST

Documentation interactive complète : **http://localhost:8080/swagger-ui.html**

Spécification OpenAPI JSON : **http://localhost:8080/api-docs**

### Authentification

| Méthode | Endpoint | Accès | Description |
|---------|----------|-------|-------------|
| `POST` | `/auth/login` | Public | Connexion — retourne un JWT |

**Exemple de requête :**
```json
POST /auth/login
{
  "email": "mia@ticketeer.com",
  "password": "user123"
}
```

**Exemple de réponse :**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": "uuid-...",
  "email": "mia@ticketeer.com",
  "role": "CUSTOMER"
}
```

---

### Trajets

| Méthode | Endpoint | Accès | Description |
|---------|----------|-------|-------------|
| `GET` | `/trips/search?from=PARIS&to=LYON&date=2026-05-02` | Public | Recherche de trajets par route et date |

**Exemple de réponse :**
```json
[
  {
    "id": "uuid-...",
    "departureStationCode": "PARIS",
    "arrivalStationCode": "LYON",
    "departureTime": "2026-05-02T08:00:00",
    "arrivalTime": "2026-05-02T10:00:00",
    "price": 45.00
  }
]
```

---

### Billets

| Méthode | Endpoint | Accès | Description |
|---------|----------|-------|-------------|
| `POST` | `/tickets` | CUSTOMER, ADMIN | Acheter un billet |
| `GET` | `/tickets/me` | CUSTOMER, ADMIN | Lister mes billets |
| `GET` | `/tickets/{id}/qr` | CUSTOMER, ADMIN | Télécharger le QR code (PNG) |
| `GET` | `/tickets/{id}/pdf` | CUSTOMER, ADMIN | Télécharger le billet PDF |

**Exemple d'achat :**
```json
POST /tickets
Authorization: Bearer <token>
{
  "holderId": "uuid-...",
  "departureStationCode": "PARIS",
  "arrivalStationCode": "LYON",
  "departureTime": "2026-05-02T08:00:00",
  "arrivalTime": "2026-05-02T10:00:00",
  "validFrom": "2026-05-01T12:00:00Z",
  "validUntil": "2026-05-02T12:00:00Z",
  "price": 45.00
}
```

---

### Contrôle

| Méthode | Endpoint | Accès | Description |
|---------|----------|-------|-------------|
| `POST` | `/control/validate` | AGENT, ADMIN | Valider un billet (par ID) |

**Exemple :**
```json
POST /control/validate
Authorization: Bearer <token>
{ "ticketId": "uuid-..." }
```

**Réponse :**
```json
{ "result": "VALID" }
// Valeurs possibles : VALID | ALREADY_CONTROLLED | EXPIRED | NOT_FOUND
```

---

## Comptes de démonstration

Créés automatiquement au démarrage (seed data) :

| Rôle | Email | Mot de passe |
|------|-------|--------------|
| Client | mia@ticketeer.com | `user123` |
| Client | lola@ticketeer.com | `user123` |
| Agent | monir@ticketeer.com | `control123` |
| Admin | ouail@ticketeer.com | `admin123` |

Les trajets seedés sont toujours dans le futur (calculés dynamiquement à partir de `LocalDate.now()`).

---

## Base de données

### Schéma (Flyway V1)

```
users
  id (UUID PK), email (unique), password_hash, role, first_name, last_name, active

stations
  code (PK), name

trips
  id (UUID PK), departure_station_code (FK), arrival_station_code (FK),
  departure_time, arrival_time, price

tickets
  id (UUID PK), holder_id (FK → users), departure/arrival station codes,
  departure/arrival times, price, valid_from, valid_until, status, issued_at

validation_records
  id (UUID PK), ticket_id (FK → tickets), agent_id (FK → users),
  validated_at, result
```

### Profils Spring

| Profil | Datasource | Activation |
|--------|------------|------------|
| `h2` (défaut) | H2 in-memory | `mvn spring-boot:run` |
| `postgres` | PostgreSQL externe | `SPRING_PROFILES_ACTIVE=postgres` |

---

## Sécurité

- **JWT (JJWT 0.12.6)** — Tokens signés HMAC-SHA256, expiration 24h
- **BCrypt** — Hachage des mots de passe (force 10)
- **HMAC-SHA256** — Signature des données QR code pour prévenir la falsification
- **Spring Security** — Filtre stateless, CORS configuré, Swagger/H2 en liste blanche
- **Validation Jakarta** — `@Valid` sur tous les DTOs d'entrée, erreurs formatées en JSON

**En production, remplacer obligatoirement :**
```yaml
# Variables d'environnement
JWT_SECRET=<clé aléatoire ≥ 32 caractères>
QR_SECRET=<clé aléatoire ≥ 32 caractères>
```

---

## Tests

```bash
cd services/backend

# Lancer tous les tests
mvn test

# Rapport de couverture (si JaCoCo configuré)
mvn verify
```

### Couverture actuelle

| Classe testée | Tests | Cas couverts |
|---------------|-------|--------------|
| `AuthenticateUserUseCase` | 6 | Login valide, email/password blank, user inconnu, inactif, mauvais password |
| `ValidateTicketUseCase` | 4 | VALID, ALREADY_CONTROLLED, EXPIRED, NOT_FOUND |
| `DateRange` | 7 | contains, bornes, null, chevauchement |

---

## Variables d'environnement

| Variable | Défaut (dev) | Description |
|----------|-------------|-------------|
| `SPRING_PROFILES_ACTIVE` | `h2` | Profil Spring (`h2` ou `postgres`) |
| `DATABASE_URL` | — | URL JDBC PostgreSQL |
| `DATABASE_USER` | — | Utilisateur PostgreSQL |
| `DATABASE_PASSWORD` | — | Mot de passe PostgreSQL |
| `JWT_SECRET` | `change-this-secret-key-for-dev-only-32chars` | Clé de signature JWT |
| `QR_SECRET` | `change-this-ticket-qr-secret-for-dev-32ch` | Clé HMAC pour QR codes |

> **Ne jamais committer les secrets en production.** Utiliser un gestionnaire de secrets (Vault, AWS Secrets Manager, variables CI/CD).

---

## Roadmap

### Court terme (soutenance)
- [x] Backend REST complet avec Clean Architecture
- [x] Authentification JWT + Spring Security
- [x] Génération QR code et PDF
- [x] Validation de billets (agents)
- [x] Client web 4 rôles (client, agent, admin, login)
- [x] Docker Compose (dev + prod)
- [x] Migrations Flyway
- [x] Documentation Swagger UI
- [x] Tests unitaires (17 tests)

### Moyen terme (mise en production)
- [ ] Refresh tokens JWT
- [ ] Interface CRUD admin (gestion gares / trajets)
- [ ] Paiement en ligne (Stripe)
- [ ] Envoi de billet par email (SMTP)
- [ ] App mobile agent (scan QR natif)
- [ ] Monitoring (Actuator + Prometheus + Grafana)
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] HTTPS / reverse proxy (Nginx / Caddy)

---

## Licence

Projet académique — tous droits réservés.

---

*Développé avec Java 17 · Spring Boot 3 · Clean Architecture · DDD*
