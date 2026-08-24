// Guest-facing copy that isn't stored in the database. Event date/venue/dress code
// come from the backend (editable via the admin dashboard) — these are just the
// fallback values shown before the backend has responded, or if it's unreachable.
export const siteConfig = {
  coupleNames: 'Akshath & Sonalika',
  weddingDateFallback: '2026-11-15T10:00:00',
  weddingVenueFallback: 'Charlotte, NC',
  story:
    "We can't wait to celebrate with the people we love most. Scroll down for all the details, and don't forget to RSVP!",
  contactEmail: 'hello@example.com',
}
