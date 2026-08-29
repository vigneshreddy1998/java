import { useEffect, useState } from 'react'
import { useParams, Navigate, Link } from 'react-router-dom'
import { api } from '../api/client.js'
import { useGuestSession } from '../session/GuestSession.jsx'
import RsvpForm from '../components/RsvpForm.jsx'

const HEADING_ACCENT = {
  wed: 'text-maroon-500',
  sangeet: 'text-sangeet-500',
  haldi: 'text-haldi-500',
  engagement: 'text-engagement-500',
}

/**
 * One page renders all four events. What appears is driven entirely by the event's flags,
 * so adding a Mehendi needs no new page — and an event the guest can't see never reaches
 * here, because the session's event list is the only source of routes.
 */
export default function EventPage() {
  const { eventKey } = useParams()
  const { events, rsvpFor, refresh } = useGuestSession()
  const [songs, setSongs] = useState([])
  const [songsError, setSongsError] = useState(null)

  const event = events.find((e) => e.key === eventKey)

  useEffect(() => {
    if (!event?.collectsSongs) return
    api
      .getSongs(event.key)
      .then(setSongs)
      .catch((e) => setSongsError(e.message))
  }, [event?.key, event?.collectsSongs])

  // Not in the session's event list means the server never granted it. Send them home
  // rather than showing an error that would confirm the event exists.
  if (!event) {
    return <Navigate to="/home" replace />
  }

  const accent = HEADING_ACCENT[event.accent] ?? HEADING_ACCENT.wed
  const rsvp = rsvpFor(event.key)

  return (
    <div className="max-w-2xl mx-auto px-6 py-14">
      <Link to="/home" className="text-sm text-ink/50 hover:text-ink transition-colors">
        ← All events
      </Link>

      <h1 className={`text-4xl font-display text-center mt-6 mb-3 ${accent}`}>{event.name}</h1>

      <div className="text-center text-ink/60 space-y-1 mb-10">
        {event.date && (
          <p>{new Date(event.date).toLocaleString(undefined, { dateStyle: 'full', timeStyle: 'short' })}</p>
        )}
        {!event.date && <p>Date coming soon</p>}
        {event.venue && <p>{event.venue}</p>}
        {event.dressCode && <p className="text-sm text-ink/50">Dress code: {event.dressCode}</p>}
        {event.colourTheme && (
          <p className="text-sm font-medium pt-2">
            <span className="text-ink/50">Colour theme: </span>
            <span className={accent}>{event.colourTheme}</span>
          </p>
        )}
      </div>

      {event.collectsRsvp ? (
        <RsvpForm
          event={event}
          existing={rsvp}
          songs={songs}
          songsError={songsError}
          onSaved={refresh}
        />
      ) : (
        <div className="rounded-xl border border-gold-300/50 bg-white p-6 text-center">
          <p className="text-ink/70">
            No need to RSVP for this one — just come and celebrate with us.
          </p>
        </div>
      )}
    </div>
  )
}
