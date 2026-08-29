// Guest-facing copy that isn't in the database. Event dates, venues, dress codes and colour
// themes all come from the backend and are edited in the admin panel — these are only the
// fallbacks shown before the backend responds.
export const siteConfig = {
  coupleNames: 'Akshath & Sonalika',
  weddingDateFallback: '2026-11-15T10:00:00',
  weddingVenueFallback: 'Charlotte, NC',
  // Set once the shared album exists; the Gallery page adapts on its own.
  galleryUrl: '',
}
