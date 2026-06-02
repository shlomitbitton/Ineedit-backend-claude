# Ineedit Backend

Spring Boot 3 · Java 21 · PostgreSQL · JWT Auth

---

## Prerequisites

- **Java 21** — [Download](https://adoptium.net/)
- **Maven 3.9+** — [Download](https://maven.apache.org/download.cgi)
- **Docker** — for the local Postgres database

Check you have them:

```bash
java -version    # should say 21
mvn -version     # should say 3.9.x
docker -version
```

---

## Quick start

```bash
# 1. Copy the example env file and fill in your values
cp .env.example .env

# 2. Start Postgres
docker-compose up -d

# 3. Run the app
mvn spring-boot:run          # if Maven is installed globally
./mvnw spring-boot:run       # Mac/Linux using the wrapper
mvnw.cmd spring-boot:run     # Windows using the wrapper

# API is now running at http://localhost:8080
```

> **Tip — Mac/Linux:** If `./mvnw` says permission denied, run `chmod +x mvnw` first.

---

## Environment variables

Copy `.env.example` to `.env` and fill in your values. Never commit `.env` to source control.

| Variable | Description |
|---|---|
| `PGHOST` | Postgres host |
| `PGPORT` | Postgres port |
| `PGDATABASE` | Postgres database name |
| `PGUSER` | Postgres username |
| `PGPASSWORD` | Postgres password |
| `JWT_SECRET` | Secret key — min 32 random chars |
| `CORS_ORIGINS` | Comma-separated list of allowed frontend origins |

---

## API reference

### Auth — public endpoints

#### POST /api/auth/register

Request:
```json
{ "name": "Your Name", "email": "you@example.com", "password": "yourpassword" }
```

Response:
```json
{ "token": "...", "user": { "id": 1, "name": "Your Name", "email": "you@example.com" } }
```

New users get three default categories automatically.

#### POST /api/auth/login

Request:
```json
{ "email": "you@example.com", "password": "yourpassword" }
```

Response: same as register.

---

All endpoints below require the header:

```
Authorization: Bearer <token>
```

### Categories

| Method | Path | Description |
|---|---|---|
| GET | /api/categories | List all categories for the logged-in user |
| POST | /api/categories | Create a new category |
| DELETE | /api/categories/{id} | Delete a category (cascades to its needs) |

POST body: `{ "name": "Category Name" }`

### Needs

| Method | Path | Description |
|---|---|---|
| GET | /api/needs?categoryId=1 | Get all needs for a category |
| POST | /api/needs | Create a need |
| PATCH | /api/needs/{id} | Update text, vendor, or toggle active |
| DELETE | /api/needs/{id} | Delete a need |

POST body:
```json
{ "text": "Item name", "vendor": "Store name", "categoryId": 1 }
```

PATCH body (all fields optional):
```json
{ "text": "Updated item", "vendor": "New store", "active": false }
```

Setting `active: false` records `fulfilledAt`. Setting back to `true` clears it.

---

## Production checklist

- [ ] Set a strong `JWT_SECRET` via env var (32+ random chars)
- [ ] Use a managed Postgres service
- [ ] Switch `ddl-auto` to `validate` and use Flyway for migrations
- [ ] Set `CORS_ORIGINS` to your production frontend URL only
- [ ] Add rate limiting on `/api/auth/**`