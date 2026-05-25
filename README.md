# ineedit backend

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
# 1. Start Postgres
docker-compose up -d

# 2. Run the app (use whichever works for you)
mvn spring-boot:run          # if Maven is installed globally (recommended)
./mvnw spring-boot:run       # Mac/Linux using the wrapper
mvnw.cmd spring-boot:run     # Windows using the wrapper

# API is now running at http://localhost:8080
```

> **Tip — Mac/Linux:** If `./mvnw` says permission denied, run `chmod +x mvnw` first.

---

## Environment variables

| Variable | Default | Description |
|---|---|---|
| `DB_USERNAME` | `postgres` | Postgres username |
| `DB_PASSWORD` | `postgres` | Postgres password |
| `JWT_SECRET` | (dev value) | **Change in production** — min 32 chars |
| `CORS_ORIGINS` | `http://localhost:3000,https://ineedit.fun` | Comma-separated allowed origins |

Override any of them inline:
```bash
DB_PASSWORD=secret mvn spring-boot:run
```

---

## API reference

### Auth — public endpoints

#### POST /api/auth/register
```json
{ "name": "Jane Smith", "email": "jane@example.com", "password": "mypassword" }
```
Response: `{ "token": "...", "user": { "id": 1, "name": "Jane Smith", "email": "jane@example.com" } }`

New users get three default categories: Shopping, Home Projects, Bills & Taxes.

#### POST /api/auth/login
```json
{ "email": "jane@example.com", "password": "mypassword" }
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

POST body: `{ "name": "Garden Projects" }`

### Needs

| Method | Path | Description |
|---|---|---|
| GET | /api/needs?categoryId=1 | Get all needs for a category |
| POST | /api/needs | Create a need |
| PATCH | /api/needs/{id} | Update text, vendor, or toggle active |
| DELETE | /api/needs/{id} | Delete a need |

POST body:
```json
{ "text": "Milk & eggs", "vendor": "Safeway", "categoryId": 1 }
```

PATCH body (all fields optional):
```json
{ "text": "Milk, eggs & butter", "vendor": "Trader Joe's", "active": false }
```

Setting `active: false` records `fulfilledAt`. Setting back to `true` clears it.

---

## Database schema

```
users         id, email, name, password (bcrypt), created_at
categories    id, name, user_id, created_at  — UNIQUE(user_id, name)
needs         id, text, vendor, active, user_id, category_id, created_at, fulfilled_at
```

Hibernate auto-creates tables on first run (`ddl-auto: update`).  
For production switch to Flyway migrations.

---

## Production checklist

- [ ] Set a strong `JWT_SECRET` via env var (32+ random chars)
- [ ] Use a managed Postgres (Supabase, Railway, AWS RDS)
- [ ] Switch `ddl-auto` to `validate` and use Flyway
- [ ] Enable HTTPS via reverse proxy (nginx, Caddy)
- [ ] Set `CORS_ORIGINS` to your production frontend URL only
- [ ] Add rate limiting on `/api/auth/**`
