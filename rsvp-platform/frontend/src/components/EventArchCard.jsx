import { Link } from 'react-router-dom'
import { useScrollReveal } from '../hooks/useScrollReveal.js'

/**
 * Accent classes are spelled out rather than built with template strings, because Tailwind's
 * scanner only keeps class names it can see literally in the source — `bg-${accent}-500`
 * would be purged from the production build.
 */
const ACCENTS = {
  wed: { panel: 'bg-maroon-500', chip: 'text-maroon-500' },
  sangeet: { panel: 'bg-sangeet-500', chip: 'text-sangeet-500' },
  haldi: { panel: 'bg-haldi-500', chip: 'text-haldi-500' },
  engagement: { panel: 'bg-engagement-500', chip: 'text-engagement-500' },
}

function daysUntil(dateString) {
  if (!dateString) return null
  const diff = Math.ceil((new Date(dateString).getTime() - Date.now()) / (1000 * 60 * 60 * 24))
  if (diff < 0) return null
  if (diff === 0) return 'Today!'
  return `${diff} day${diff === 1 ? '' : 's'}`
}

export default function EventArchCard({ event, rsvp, delayMs = 0 }) {
  const [ref, inView] = useScrollReveal()
  const styles = ACCENTS[event.accent] ?? ACCENTS.wed
  const pill = daysUntil(event.date)

  const month = event.date
    ? new Date(event.date).toLocaleDateString(undefined, { month: 'short' }).toUpperCase()
    : null
  const day = event.date ? new Date(event.date).getDate() : null

  const statusLabel =
    rsvp?.status === 'ACCEPTED' ? 'You’re going' : rsvp?.status === 'DECLINED' ? 'Not attending' : null

  return (
    // The date chip sits outside the masked shape on purpose — a CSS mask clips everything
    // painted inside its box, so a chip poking above the arch would be cropped away.
    <article
      ref={ref}
      className={`reveal ${inView ? 'in-view' : ''} relative w-[260px]`}
      style={{ transitionDelay: `${delayMs}ms` }}
    >
      <div className="absolute -top-5 left-4 z-10 w-11 h-11 rounded-lg bg-white shadow-soft flex flex-col items-center justify-center">
        {day !== null ? (
          <>
            <span className={`text-[8.5px] font-bold tracking-wide ${styles.chip}`}>{month}</span>
            <span className="text-sm font-extrabold leading-none text-ink">{day}</span>
          </>
        ) : (
          <span className={`text-[9px] font-bold tracking-wide ${styles.chip}`}>TBD</span>
        )}
      </div>

      <Link to={`/event/${event.key}`} className="block mask-arch shadow-card flex flex-col">
        <div
          className="texture-noise h-56 shrink-0 bg-gradient-to-br from-parchment to-sand flex items-end p-2.5"
          role="img"
          aria-label={`${event.name} photo`}
        >
          <span className="relative z-[1] text-[9.5px] uppercase tracking-wide text-ink/50">
            {event.name}
          </span>
          {pill && (
            <span className="absolute top-2.5 right-2.5 z-[1] bg-white/90 px-2.5 py-0.5 rounded-full text-[10px] font-semibold text-ink">
              {pill}
            </span>
          )}
        </div>

        <div className={`px-4 pt-9 pb-5 text-white relative ${styles.panel}`}>
          <h3 className="font-display font-bold text-lg mb-2">{event.name}</h3>
          <p className="text-[10px] uppercase tracking-wide text-white/70 mb-0.5">When</p>
          <p className="text-[12.5px] mb-2">
            {event.date
              ? new Date(event.date).toLocaleString(undefined, { dateStyle: 'medium', timeStyle: 'short' })
              : 'Date coming soon'}
          </p>
          <p className="text-[10px] uppercase tracking-wide text-white/70 mb-0.5">Where</p>
          <p className="text-[12.5px]">{event.venue || 'TBA'}</p>

          {statusLabel ? (
            <p className="text-[11px] mt-3 font-medium text-white/90">{statusLabel}</p>
          ) : event.collectsRsvp ? (
            <p className="text-[11px] mt-3 text-white/70">Tap to RSVP</p>
          ) : (
            <p className="text-[11px] mt-3 text-white/70">Tap for details</p>
          )}
        </div>
      </Link>
    </article>
  )
}
