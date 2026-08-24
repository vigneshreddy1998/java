# RSVP Platform

A full-stack Wedding + Sangeet RSVP site: React (Vite + Tailwind) frontend, Spring Boot
backend, PostgreSQL database, plus an in-site AI chat widget for collecting flight
details after a guest RSVPs.

```
rsvp-platform/
├── backend/     Spring Boot REST API (JWT-secured admin routes)
└── frontend/    React site (home, wedding, sangeet, travel, gallery, admin dashboard)
```

## Data model

`Family` (unique invite token) → `Guest`s → `Rsvp` per `Event` (Wedding/Sangeet).
`Song`s can be claimed by a family for the Sangeet performance list (claim is atomic —
concurrent claims on the same song can't both win). `FlightDetail` is populated either
by the AI chat widget or a manual fallback form, keyed by guest.

## 1. Local development

**Database**
```bash
docker run --name rsvp-db -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=rsvp_platform -p 5432:5432 -d postgres
```

**Backend**
```bash
cd backend
cp .env.example .env      # fill in JWT_SECRET, ADMIN_BOOTSTRAP_PASSWORD, ANTHROPIC_API_KEY
export $(cat .env | xargs)
./mvnw spring-boot:run     # or `mvn spring-boot:run`
```
Runs on `http://localhost:8081`. Tables auto-create on first run (`ddl-auto: update`);
the Wedding/Sangeet events and the bootstrap admin user are seeded automatically.

**Frontend**
```bash
cd frontend
cp .env.local.example .env.local
npm install
npm run dev
```
Runs on `http://localhost:5173`.

## 2. Load your guest list

Log in to `/admin/login` with the bootstrap admin credentials, then use the **Import**
tab to upload a CSV (`family_name,guest_name,meal_pref,language_pref`). Guests sharing
a `family_name` are grouped under one invite link — the response table gives you the
`/rsvp/<token>` link to send each family.

## 3. AI chat widget

When a guest accepts the Wedding RSVP, a chat widget opens asking about their flight so
the couple can plan pickup. It calls the Anthropic Messages API (model + key configured
via `LLM_MODEL` / `ANTHROPIC_API_KEY`) with a `record_flight_details` tool; once enough
fields are gathered it saves a `FlightDetail` row. If `ANTHROPIC_API_KEY` is unset, or the
call fails, the widget shows a manual form instead — flight-detail capture never blocks
on the AI being available.

## 4. Song picker

`POST /api/songs/{id}/claim` does a conditional `UPDATE ... WHERE claimed_by_family_id
IS NULL` — under concurrent claims, only one request's update affects a row, so exactly
one family wins and the other gets a 409. The Sangeet page polls the song list every few
seconds so "taken" status stays live across guests.

## 5. Deploy

Same playbook as other projects in this repo: Postgres on Railway/Render/Supabase,
backend on Railway/Render (`/backend` as root, set the env vars from `.env.example`),
frontend on Vercel (`/frontend` as root, `VITE_API_URL` pointed at the backend URL, and
set `FRONTEND_ORIGIN` on the backend to the resulting Vercel URL for CORS).

## API reference

| Method | Path | Auth | Purpose |
|---|---|---|---|
| GET | `/api/events` | — | Wedding + Sangeet event details |
| GET | `/api/families/{inviteToken}` | — | Family + guests + existing RSVPs |
| POST | `/api/rsvp` | — | Submit/update a guest's RSVP for an event |
| GET | `/api/songs` | — | Song list with locked/claimed status |
| POST | `/api/songs/{id}/claim` | — | Atomically claim a song for a family |
| POST | `/api/chat/flight-details` | — | AI chat turn; returns a reply or a saved extraction |
| POST | `/api/flight-details` | — | Manual flight-detail fallback form |
| POST | `/api/admin/auth/login` | — | Admin login, returns a JWT |
| GET | `/api/admin/rsvps` | JWT | RSVP tracker (filter by `eventType`, `status`) |
| GET | `/api/admin/meals-summary` | JWT | Meal/dietary summary for an event |
| GET | `/api/admin/logistics` | JWT | Flight details sorted by arrival |
| GET | `/api/admin/non-responders` | JWT | Guests with no accepted/declined RSVP |
| POST | `/api/admin/reminders/send` | JWT | Log a reminder batch (no channel wired up yet) |
| POST | `/api/admin/songs` | JWT | Add a song to the Sangeet list |
| PUT | `/api/admin/events/{type}` | JWT | Update an event's name/date/venue/dress code |
| POST | `/api/admin/guests/import` | JWT | CSV guest/family import, returns invite links |
