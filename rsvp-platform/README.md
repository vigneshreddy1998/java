# RSVP Platform

A full-stack site for a four-event wedding: React (Vite + Tailwind) frontend,
Spring Boot backend, PostgreSQL database.

```
rsvp-platform/
├── backend/     Spring Boot REST API
└── frontend/    React site (guest journey + admin dashboard)
```

## How access works

There is **one shared link** — no per-guest URL, no password. A guest enters their
phone number, the server resolves it against your list, and issues a short-lived
session token carrying the events that guest may see. The token lives in browser
memory only, so a refresh re-verifies.

A number **not** on your list still gets in, but only to the open events, and is
flagged `SELF_REGISTERED` in the admin so your headcount stays honest. Both paths look
identical to the visitor, so nobody can probe who was invited.

## The four events

Events are configured by data, not named in code — adding a Mehendi is a row in the
`events` table, not a release.

| Event | Invite required | Takes RSVP | Meal | Songs |
|---|---|---|---|---|
| Engagement | — | — | — | — |
| Haldi | — | — | — | — |
| Sangeet | **Yes** | Yes | Yes | Yes |
| Wedding | — | Yes | Yes | — |

Sangeet is the only gated event. A guest without an invite gets a **404** on every
Sangeet route — enforced server-side against the session, not by omitting the nav link.

## 1. Local development

**Database** — Docker:
```bash
docker run --name rsvp-db -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=rsvp_platform -p 5432:5432 -d postgres
```
or a native Postgres install with a `rsvp_platform` database created.

**Backend**
```bash
cd backend
cp .env.example .env      # fill in JWT_SECRET, ADMIN_BOOTSTRAP_PASSWORD, ANTHROPIC_API_KEY
export $(cat .env | xargs) # PowerShell: set them in the run configuration instead
./mvnw spring-boot:run     # or `mvn spring-boot:run`
```
Runs on `http://localhost:8081`. Tables auto-create on first run (`ddl-auto: update`);
the four events and the bootstrap admin user are seeded automatically.

**Frontend**
```bash
cd frontend
cp .env.local.example .env.local
npm install
npm run dev
```
Runs on `http://localhost:5173`.

### If the backend fails to start after a `git pull`

A stack trace like this means your database still holds the **previous** schema:

```
Error executing DDL "alter table if exists events add column accent varchar(255) not null"
  [ERROR: column "accent" of relation "events" contains null values]
```

`ddl-auto: update` cannot add a `NOT NULL` column to a table that already has rows, and
before launch the schema still changes shape. There is nothing to migrate — reset it:

```sql
-- connect to the rsvp_platform database, then:
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
```

Stop the backend first, run that, then start it again — the tables and the four events
are recreated on boot. **This deletes every guest and RSVP in that database**, which is
fine while you are testing and not fine once real guests have replied.

Once you go live, replace `ddl-auto: update` with real migrations (Flyway or Liquibase)
so schema changes stop being destructive.

## 2. Load your guest list

Log in at `/admin/login` with the bootstrap admin credentials, then use the **Import**
tab:

1. Export your phone contacts as a `.vcf` file and upload it.
2. Review the parsed rows — names are tidied for you to accept or correct, unusable
   numbers are flagged, and duplicates are grouped.
3. Tick which events each contact is invited to, then import.

Nothing is saved until you press import. Re-importing is safe: an existing guest keeps
their identity and RSVPs, and only their name and invites are updated.

Phone numbers are normalised to E.164 by libphonenumber on both import and guest entry,
with a fallback match on the trailing ten digits — so a number saved as `(704) 555-0100`
still resolves when the guest types `+1 704 555 0100`.

## 3. Contact-name cleanup

At import, contact names are cleaned up via the Anthropic Messages API — "Ravi Anna" and
"SHARMA UNCLE - OFFICE" become names you would put on a place card. Every suggestion is
reviewed by you before anything is saved. With no `ANTHROPIC_API_KEY` set, or if the call
fails, import proceeds with the original names untouched.

Scope is deliberately names only. Phone numbers are never touched by the model.

## 4. Songs

Song picks are a plain many-to-many — nothing locks, and two guests may pick the same
song. The admin **Songs** tab surfaces any overlap so it is found before the night rather
than during it.

## 5. Deploy

Postgres on Railway/Render/Supabase, backend on Railway/Render (`/backend` as root, env
vars from `.env.example`), frontend on Vercel (`/frontend` as root, `VITE_API_URL`
pointed at the backend, and `FRONTEND_ORIGIN` on the backend set to the Vercel URL for
CORS).

## API reference

Guest routes need the session token from `/api/verify`; admin routes need the JWT from
the admin login.

| Method | Path | Auth | Purpose |
|---|---|---|---|
| POST | `/api/verify` | — | Phone number in, guest session out |
| GET | `/api/me` | Guest | Name, consent, visible events, existing RSVPs |
| GET | `/api/events/{key}` | Guest | One event — 404 if not visible to this guest |
| GET | `/api/events/{key}/songs` | Guest | Song list with the guest's own picks marked |
| POST | `/api/events/{key}/rsvp` | Guest | Submit or update an RSVP |
| POST | `/api/admin/auth/login` | — | Admin login, returns a JWT |
| GET | `/api/admin/overview` | JWT | Per-event counts, headcount, meal split |
| GET | `/api/admin/guests` | JWT | Guest table with invites and RSVP summaries |
| PUT | `/api/admin/guests/{id}/invites` | JWT | Replace a guest's invited events |
| POST | `/api/admin/guests/{id}/promote` | JWT | Move a self-registered guest onto your list |
| POST | `/api/admin/contacts/preview` | JWT | Parse a `.vcf` and propose cleaned names |
| POST | `/api/admin/contacts/commit` | JWT | Save the approved import rows |
| GET | `/api/admin/songs` | JWT | Songs with pickers and duplicate flags |
| POST | `/api/admin/songs` | JWT | Add a song |
| DELETE | `/api/admin/songs/{id}` | JWT | Remove a song |
| GET | `/api/admin/events` | JWT | All events with their behaviour flags |
| PUT | `/api/admin/events/{key}` | JWT | Update name, date, venue, dress code, colour theme |
