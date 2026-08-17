# Wedding RSVP

A full-stack RSVP site: React (Vite) frontend, Spring Boot backend, PostgreSQL database.
Same deployment pattern as the AI Job Copilot stack — Railway (backend) + Vercel (frontend) + Supabase (database).

```
wedding-rsvp/
├── backend/     Spring Boot REST API
└── frontend/    React site (landing page, RSVP form, admin dashboard)
```

## 1. Customize your content

Everything guest-facing lives in **one file**: `frontend/src/config/eventConfig.js`.
Edit couple names, wedding date, venue, schedule, dress code, meal options, and
your contact email there. No other file needs to change for basic customization.

Design tokens (colors/fonts) are in `frontend/src/styles/tokens.css` if you want
to adjust the palette.

### Sangeeth Night invite (`/sangeeth`)

A second, self-contained page at `/sangeeth` — a dark, "music player" style
invite (press play → track plays → "Are you IN?" → RSVP). Edit its couple
names, date, time, photo, and party size cap in the `sangeethConfig` export
at the bottom of `frontend/src/config/eventConfig.js`. Swap the photo by
replacing `frontend/src/assets/sangeeth-photo.jpg`.

Its RSVPs post to the same `/api/rsvp` endpoint as the main wedding form,
tagged with `eventType: "SANGEETH"` so they're stored in the same database
and show up in `/admin` alongside (and filterable from) the main wedding
RSVPs — see the API reference below.

## 2. Local development

**Database** — easiest is a free [Supabase](https://supabase.com) Postgres project,
or run Postgres locally with Docker:
```bash
docker run --name wedding-db -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=wedding_rsvp -p 5432:5432 -d postgres
```

**Backend**
```bash
cd backend
cp .env.example .env      # fill in DATABASE_URL / ADMIN_KEY
export $(cat .env | xargs)
./mvnw spring-boot:run    # or `mvn spring-boot:run` if you have Maven installed
```
Runs on `http://localhost:8080`. Tables are auto-created on first run (`ddl-auto: update`).

**Frontend**
```bash
cd frontend
cp .env.local.example .env.local
npm install
npm run dev
```
Runs on `http://localhost:5173`.

## 3. Load your guest list (optional but recommended)

If you want to prevent randoms from RSVPing, set `requireInviteCode: true` in
`eventConfig.js`, then bulk-add guests with a unique code per household:

```bash
curl -X POST http://localhost:8080/api/admin/guests/bulk \
  -H "X-Admin-Key: YOUR_ADMIN_KEY" \
  -H "Content-Type: application/json" \
  -d '[
    { "partyName": "The Sharma Family", "maxGuests": 4, "email": "sharma@example.com" },
    { "partyName": "Alex Chen", "maxGuests": 2, "email": "alex@example.com" }
  ]'
```

Leave `inviteCode` out and the server generates a random 6-character code per
guest — check the response for what to print on each invite. Leave
`requireInviteCode: false` (the default) if you'd rather everyone just RSVP
with their name and email via the open link.

## 4. Deploy

**Database — Supabase**
1. Create a project at supabase.com, grab the Postgres connection string.

**Backend — Railway**
1. New project → Deploy from GitHub repo → point at `/backend`.
2. Set env vars: `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` (from
   Supabase), `ADMIN_KEY` (a long random string — this guards `/api/admin/**`),
   `FRONTEND_ORIGIN` (your Vercel URL, added after step below).
3. Railway auto-detects the Maven build. Note the public backend URL it gives you.

**Frontend — Vercel**
1. New project → point at `/frontend` as the root directory.
2. Framework preset: Vite. Build command `npm run build`, output `dist`.
3. Set env var `VITE_API_URL` to your Railway backend URL.
4. Deploy, then go back to Railway and set `FRONTEND_ORIGIN` to the resulting
   Vercel URL so CORS allows it.

## 5. Share it

Send guests `https://your-site.vercel.app`. The admin dashboard is at
`/admin`, gated behind the `ADMIN_KEY` you set — that's where you see live
responses, headcounts, and a CSV export button for your caterer/venue.

## API reference

| Method | Path | Auth | Purpose |
|---|---|---|---|
| GET | `/api/guests/lookup?code=` | — | Look up a party by invite code |
| POST | `/api/rsvp` | — | Submit or update an RSVP. Accepts an optional `eventType` (defaults to `"WEDDING"`; the Sangeeth page sends `"SANGEETH"`). Upserts by email when provided, otherwise by guest name within that event. |
| GET | `/api/rsvp/check?email=&eventType=` | — | Check for an existing response for that email + event |
| GET | `/api/admin/rsvps` | `X-Admin-Key` | All responses + summary counts |
| GET | `/api/admin/export` | `X-Admin-Key` | CSV download |
| POST | `/api/admin/guests` | `X-Admin-Key` | Add one guest/household |
| POST | `/api/admin/guests/bulk` | `X-Admin-Key` | Add many guests at once |
