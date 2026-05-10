# Ticketeer — Système de Billetterie Ferroviaire

> Plateforme complète de billetterie ferroviaire : achat de billets, génération QR/PDF, validation en temps réel par app mobile et tableau de bord administrateur.

---

## Table des matières

1. [Vue d'ensemble](#vue-densemble)
2. [Architecture](#architecture)
3. [Structure du projet](#structure-du-projet)
4. [Stack technique](#stack-technique)
5. [Démarrage rapide](#démarrage-rapide)
6. [Client web](#client-web)
7. [App mobile agent](#app-mobile-agent)
8. [API REST](#api-rest)
9. [Comptes de démonstration](#comptes-de-démonstration)
10. [Base de données](#base-de-données)
11. [Sécurité](#sécurité)
12. [Tests](#tests)
13. [Variables d'environnement](#variables-denvironnement)
14. [Roadmap](#roadmap)

---

## Vue d'ensemble

Ticketeer est un système de billetterie ferroviaire complet conçu selon les principes du **Clean Architecture** et du **Domain-Driven Design**. Il couvre l'ensemble du cycle de vie d'un billet : recherche de trajet → achat → téléchargement QR/PDF → validation par un agent via app mobile.

```
┌─────────────────────────────────────────────────────────────┐
│                    CLIENT WEB (Vanilla JS)                   │
│   Login  │  Espace Client  │  Contrôle Agent  │  Admin      │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────┤  HTTP / REST + JWT
│                        │
│  ┌─────────────────────▼────────────────────────────────┐   │
│  │              BACKEND API (Spring Boot 3)              │   │
│  │  identity │ network │ ticketing │ control │ security  │   │
│  └─────────────────────┬────────────────────────────────┘   │
│                        │ JPA / Flyway                        │
│  ┌─────────────────────▼────────────────────────────────┐   │
│  │         PostgreSQL 16  (H2 in-memory en dev)         │   │
│  └──────────────────────────────────────────────────────┘   │
│                                                              │
└──────────────────────────────────────────────────────────────┘
                         ▲
                         │ HTTP / REST + JWT
┌────────────────────────┴────────────────────────────────────┐
│              APP MOBILE AGENT (React Native + Expo)          │
│         Login  │  Scanner QR  │  Résultat validation         │
└─────────────────────────────────────────────────────────────┘
```

---

## Architecture

Le backend est un **monolithe modulaire** organisé en contextes bornés (bounded contexts) indépendants. Chaque contexte suit le pattern **Ports & Adapters (Hexagonale)** :

```
domain/          ← entités, value objects, règles métier pures
application/     ← use cases, ports (interfaces)
infrastructure/  ← adapters (JPA, JWT, QR, PDF…)
api/rest/        ← controllers, DTOs
```

### Contextes bornés

| Contexte | Responsabilité |
|----------|----------------|
| **identity** | Authentification JWT, gestion des utilisateurs, BCrypt |
| **network** | Gares (10 villes), trajets, recherche par date/route |
| **ticketing** | Émission de billets, génération QR (ZXing), PDF (OpenPDF) |
| **control** | Validation de billets par les agents, audit log |
| **shared** | Exceptions domaine, `DateRange`, `DomainClock`, `GlobalExceptionHandler` |
| **security** | Filtre JWT Spring Security, configuration CORS |

---

## Structure du projet

```
ticketeer-system/
├── apps/
│   ├── web-client/               # SPA Vanilla JS
│   │   ├── index.html            # Page de connexion
│   │   ├── customer.html         # Espace client (recherche + billets)
│   │   ├── admin.html            # Tableau de bord admin
│   │   ├── dashboard.html        # Vue trajets admin
│   │   ├── css/style.css
│   │   └── js/api.js             # Client HTTP + utilitaires
│   │
│   └── agent-mobile/             # App mobile React Native (Expo)
│       ├── App.js                # Navigation principale
│       ├── app.json              # Config Expo (SDK 54)
│       ├── package.json
│       └── src/
│           ├── config.js         # URL du backend (à adapter)
│           ├── api.js            # Appels HTTP (login + validate)
│           └── screens/
│               ├── LoginScreen.js    # Authentification agent
│               ├── ScannerScreen.js  # Scanner QR caméra
│               └── ResultScreen.js   # Résultat VALID / EXPIRED / ALREADY_CONTROLLED
│
├── services/
│   └── backend/                  # API Spring Boot 3
│       ├── src/main/java/com/ticketeer/
│       │   ├── identity/
│       │   ├── network/
│       │   ├── ticketing/
│       │   ├── control/
│       │   ├── shared/
│       │   ├── security/
│       │   ├── bootstrap/        # DataInitializer (seed au démarrage)
│       │   └── config/           # AppConfig (câblage des beans)
│       ├── src/main/resources/
│       │   ├── application.yml
│       │   ├── application-h2.yml
│       │   ├── application-postgres.yml
│       │   └── db/migration/V1__init_schema.sql
│       ├── src/test/             # Tests unitaires JUnit 5
│       ├── Dockerfile
│       └── pom.xml
│
├── docs/
│   └── domain/
│       ├── backend-domain-model.md
│       └── shared-kernel.md
├── docker-compose.yml
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

### Client web

| Technologie | Usage |
|-------------|-------|
| HTML5 / CSS3 / Vanilla JS | SPA sans framework |
| Fetch API | Appels HTTP vers le backend |

### App mobile

| Technologie | Version | Usage |
|-------------|---------|-------|
| React Native | 0.77.1 | Framework mobile |
| Expo | SDK 54 | Outillage & build |
| expo-camera | 16.x | Scanner QR code via caméra |
| React Navigation | v6 | Navigation entre écrans |
| AsyncStorage | 2.x | Stockage local du JWT |

### Infrastructure

| Technologie | Usage |
|-------------|-------|
| Docker | Conteneurisation backend |
| Docker Compose | Orchestration locale (DB + API) |

---

## Démarrage rapide

### Prérequis

- Java 17+
- Maven 3.8+
- Python 3 (pour servir le client web)
- Node.js 18+ (pour l'app mobile)
- Expo Go installé sur le téléphone ([iOS](https://apps.apple.com/app/expo-go/id982107779) / [Android](https://play.google.com/store/apps/details?id=host.exp.exponent))

---

### Terminal 1 — Backend

```bash
cd services/backend
mvn spring-boot:run
```

✅ API disponible sur **http://localhost:8080**  
📖 Swagger UI : **http://localhost:8080/swagger-ui.html**  
🗄️ H2 Console : **http://localhost:8080/h2-console** (JDBC URL: `jdbc:h2:mem:ticketeerdb`, user: `sa`, password: vide)

> Les données de démonstration (utilisateurs, gares, trajets) sont seedées automatiquement au démarrage.

---

### Terminal 2 — Client web

```bash
cd apps/web-client
python -m http.server 8001
```

✅ Disponible sur **http://localhost:8001/customer.html**

---

### Terminal 3 — App mobile agent (optionnel)

```bash
cd apps/agent-mobile
npm install --legacy-peer-deps
npx expo start
```

1. Scanner le QR code affiché dans le terminal avec **Expo Go**
2. L'app se charge sur le téléphone

> ⚠️ **Important** : le téléphone et le PC doivent être sur le **même réseau Wi-Fi**.  
> ⚠️ Modifier `src/config.js` avec l'adresse IP Wi-Fi du PC (commande `ipconfig` sur Windows).

```js
// src/config.js
export const API_BASE_URL = 'http://<VOTRE_IP_WIFI>:8080';
```

---

### Mode production (PostgreSQL + Docker)

```bash
# Tout démarrer
docker compose up --build

# Arrêter
docker compose down

# Reset complet de la base
docker compose down -v
```

---

## Client web

| Page | URL | Rôle | Fonctionnalités |
|------|-----|------|-----------------|
| `index.html` | `/index.html` | Public | Connexion, redirection par rôle |
| `customer.html` | `/customer.html` | CUSTOMER, ADMIN | Recherche trajet, achat billet, liste billets, QR/PDF |
| `admin.html` | `/admin.html` | ADMIN | Recherche trajets, gestion |
| `dashboard.html` | `/dashboard.html` | ADMIN | Vue tableau des trajets |

---

## App mobile agent

L'app mobile est destinée aux **agents contrôleurs**. Elle permet de scanner le QR code d'un billet et d'obtenir instantanément son statut de validité.

### Flux de l'app

```
[Login] → [Scanner QR] → [Résultat]
                              ↓
               ✅ BILLET VALIDE
               ⏰ BILLET EXPIRÉ
               🚫 DÉJÀ CONTRÔLÉ
               ❌ ERREUR
```

### Écrans

| Écran | Description |
|-------|-------------|
| **LoginScreen** | Email + mot de passe, pré-rempli avec le compte agent |
| **ScannerScreen** | Caméra plein écran avec cadre de scan, détection QR automatique |
| **ResultScreen** | Résultat coloré avec détails du billet (passager, validité) |

### Compte agent par défaut

```
Email    : monir@ticketeer.com
Password : control123
```

---

## API REST

Documentation interactive complète : **http://localhost:8080/swagger-ui.html**

### Authentification

| Méthode | Endpoint | Accès | Description |
|---------|----------|-------|-------------|
| `POST` | `/auth/login` | Public | Retourne un JWT |

```json
POST /auth/login
{ "email": "mia@ticketeer.com", "password": "user123" }

→ { "token": "eyJhbGciOiJIUzI1NiJ9..." }
```

### Trajets

| Méthode | Endpoint | Accès | Description |
|---------|----------|-------|-------------|
| `GET` | `/trips/search?from=PARIS&to=LYON&date=2026-05-07` | Public | Recherche par route et date |

### Billets

| Méthode | Endpoint | Accès | Description |
|---------|----------|-------|-------------|
| `POST` | `/tickets` | CUSTOMER, ADMIN | Acheter un billet |
| `GET` | `/tickets/me` | CUSTOMER, ADMIN | Mes billets |
| `GET` | `/tickets/{id}/qr` | CUSTOMER, ADMIN | QR code (PNG) |
| `GET` | `/tickets/{id}/pdf` | CUSTOMER, ADMIN | Billet PDF |

### Contrôle

| Méthode | Endpoint | Accès | Description |
|---------|----------|-------|-------------|
| `POST` | `/control/validate` | AGENT, ADMIN | Valider un billet |

```json
POST /control/validate
Authorization: Bearer <token>
{ "ticketId": "uuid-..." }

→ { "result": "VALID" }
// Valeurs : VALID | ALREADY_CONTROLLED | EXPIRED
```

---

## Comptes de démonstration

Créés automatiquement au démarrage :

| Rôle | Email | Mot de passe |
|------|-------|--------------|
| Client | mia@ticketeer.com | `user123` |
| Client | lola@ticketeer.com | `user123` |
| Client | rihabe@ticketeer.com | `user123` |
| Agent | monir@ticketeer.com | `control123` |
| Admin | ouail@ticketeer.com | `admin123` |

---

## Base de données

### Schéma (Flyway V1)

```
users               → id, email, password_hash, role, first_name, last_name, active
stations            → code (PK), name  [10 villes françaises]
trips               → id, departure/arrival station codes, departure/arrival times, price
tickets             → id, holder_id, stations, times, price, valid_from, valid_until, status, issued_at
validation_records  → id, ticket_id, agent_id, validated_at, result
```

### Données seedées

- **10 gares** : Paris, Lyon, Marseille, Lille, Bordeaux, Nantes, Toulouse, Strasbourg, Nice, Rennes
- **20 trajets** répartis sur J+1, J+2, J+3 (toujours dans le futur)

### Profils Spring

| Profil | Datasource | Activation |
|--------|------------|------------|
| `h2` (défaut) | H2 in-memory | `mvn spring-boot:run` |
| `postgres` | PostgreSQL externe | `SPRING_PROFILES_ACTIVE=postgres` |

---

## Sécurité

- **JWT (JJWT 0.12.6)** — Tokens signés HMAC-SHA256, expiration 24h
- **BCrypt** — Hachage des mots de passe (force 10)
- **HMAC-SHA256** — Signature des QR codes pour prévenir la falsification
- **Spring Security** — Filtre stateless, CORS configuré (`*`)
- **Validation Jakarta** — `@Valid` sur tous les DTOs

**Secrets à changer en production :**
```yaml
JWT_SECRET=<clé aléatoire ≥ 32 caractères>
QR_SECRET=<clé aléatoire ≥ 32 caractères>
```

---

## Tests

```bash
cd services/backend
mvn test
```

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
| `JWT_SECRET` | `change-this-secret-key-for-dev-only-32chars` | Clé JWT |
| `QR_SECRET` | `change-this-ticket-qr-secret-for-dev-32ch` | Clé HMAC QR |

---

## Roadmap

### Court terme — Soutenance ✅

- [x] Backend REST complet avec Clean Architecture + DDD
- [x] Authentification JWT + Spring Security (3 rôles)
- [x] 10 villes, 20 trajets seedés dynamiquement
- [x] Génération QR code signé (HMAC) et PDF (nom passager, trajet, prix)
- [x] Validation de billets par agent
- [x] Client web 4 rôles (client, agent, admin, login)
- [x] **App mobile agent** (React Native + Expo, iOS & Android — scan QR natif)
- [x] Docker Compose (dev + prod)
- [x] Migrations Flyway
- [x] Documentation Swagger UI
- [x] Tests unitaires (17 tests)

### Moyen terme — Mise en production

- [ ] Refresh tokens JWT
- [ ] Interface CRUD admin (gestion gares / trajets)
- [ ] Paiement en ligne (Stripe)
- [ ] Envoi de billet par email (SMTP)
- [ ] Monitoring (Actuator + Prometheus + Grafana)
- [ ] CI/CD pipeline (GitHub Actions)
- [ ] HTTPS / reverse proxy (Nginx / Caddy)

---

## Scénario de démo

1. Ouvrir `http://localhost:8001/customer.html`
2. Se connecter avec `mia@ticketeer.com` / `user123`
3. Rechercher **Paris → Lyon** pour demain
4. Acheter un billet → télécharger le **QR code** ou le **PDF**
5. Sur iPhone/Android → ouvrir **Ticketeer Agent**
6. Se connecter avec `monir@ticketeer.com` / `control123`
7. Scanner le QR code → ✅ **BILLET VALIDE**
8. Rescanner le même QR → 🚫 **DÉJÀ CONTRÔLÉ**

---

*Développé avec Java 17 · Spring Boot 3 · React Native · Clean Architecture · DDD*
