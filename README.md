# Project-Login-React-Springboot

A fullstack web application with JWT-based authentication, built with a Spring Boot (Kotlin) backend and a React (TypeScript) frontend.

---

## Tech Stack

**Backend (`/server`)**

- Kotlin + Spring Boot 4
- Spring Security + BCrypt
- JWT (Access Token + Refresh Token)
- MongoDB (via Spring Data MongoDB)
- Gradle (Kotlin DSL)

**Frontend (`/web`)**

- React 19 + TypeScript
- Vite 8
- Tailwind CSS v4 + shadcn/ui (Radix UI)
- React Router v7
- React Hook Form
- Axios

---

## Architecture Overview

Authentication follows a dual-token pattern:

- **Access Token** — short-lived (15 min), stored in React state (in-memory), attached to requests via `Authorization: Bearer` header.
- **Refresh Token** — long-lived (30 days), stored as an `HttpOnly` cookie, used to silently renew the access token via `GET /auth/me`.
- Refresh tokens are hashed (SHA-256) before being stored in MongoDB and are rotated on every use.

---

## Getting Started

### Prerequisites

- Java 17+
- Node.js 20+
- MongoDB instance

---

### Backend Setup

1. Navigate to the server directory:

```bash
cd server
```

2. Set the following environment variables at `server/src/main/resources/application.properties`:

```env
JWT_SECRET_BASE64=<your-base64-encoded-secret>
MONGO_URI=<your-mongodb-connection-string>
```

3. Run the application:

```bash
./gradlew bootRun
```

The server starts on `http://localhost:8080` by default.

---

### Frontend Setup

1. Navigate to the web directory:

```bash
cd web
```

2. Install dependencies:

```bash
npm install
```

3. Create a `.env` file based on the example:

```bash
cp .env.example .env
```

Then fill in:

```env
VITE_BASE_API=http://localhost:8080
```

4. Start the development server:

```bash
npm run dev
```

The app runs on `http://localhost:5173` by default.

---

## API Endpoints

| Method | Path             | Description                                    |
| ------ | ---------------- | ---------------------------------------------- |
| `POST` | `/auth/register` | Register a new user                            |
| `POST` | `/auth/login`    | Login and receive access + refresh tokens      |
| `GET`  | `/auth/me`       | Refresh access token using the HttpOnly cookie |

---

## Validation Rules

**Registration**

- `username` — 3–20 characters, letters/numbers/underscores only
- `email` — valid email format
- `password` — minimum 8 characters, at least 1 uppercase, 1 lowercase, 1 number

**Login**

- `email` — valid email format
- `password` — non-blank

---

## Environment Variables

| Variable                 | Location | Description                                 |
| ------------------------ | -------- | ------------------------------------------- |
| `JWT_SECRET_BASE64`      | server   | Base64-encoded HMAC secret for signing JWTs |
| `MONGO_URI`              | server   | MongoDB connection URI                      |
| `SPRING_PROFILES_ACTIVE` | server   | Set to `prod` to enable secure cookies      |
| `VITE_BASE_API`          | web      | Base URL of the backend API                 |

---

## Production Notes

- Set `SPRING_PROFILES_ACTIVE=prod` on the server to enable `Secure` and `SameSite=Strict` flags on the refresh token cookie.
- CORS is configured to allow `http://localhost:5173` by default — update `SecurityConfig.kt` for your production domain.
- MongoDB TTL index on `RefreshToken.expiresAt` automatically removes expired tokens.
