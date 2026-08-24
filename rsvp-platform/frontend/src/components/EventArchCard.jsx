import { useScrollReveal } from '../hooks/useScrollReveal.js'

// Tailwind's JIT scanner needs full class names present as literal strings
// somewhere in the source — building them with template strings like
// `bg-${accent}-500` would get purged in production. This lookup keeps every
// variant's classes spelled out so they survive the build.
const ACCENT_STYLES = {
  wed: {
    panel: 'bg-maroon-500',
    chipText: 'text-maroon-500',
  },
  sangeet: {
    panel: 'bg-sangeet-500',
    chipText: 'text-sangeet-500',
  },
}

function daysUntil(dateString) {
  if (!dateString) return null
  const diff = Math.ceil((new Date(dateString).getTime() - Date.now()) / (1000 * 60 * 60 * 24))
  if (diff < 0) return null
  if (diff === 0) return 'Today!'
  return `${diff} day${diff === 1 ? '' : 's'}`
}

export default function EventArchCard({
  name,
  date,
  venue,
  dressCode,
  accent = 'wed',
  photoLabel,
  delayMs = 0,
}) {
  const [ref, inView] = useScrollReveal()
  const styles = ACCENT_STYLES[accent] ?? ACCENT_STYLES.wed
  const pill = daysUntil(date)

  const month = date ? new Date(date).toLocaleDateString(undefined, { month: 'short' }).toUpperCase() : null
  const day = date ? new Date(date).getDate() : null

  return (
    // The date chip is deliberately a sibling of the masked shape, not a child of it —
    // CSS mask/clip applies to everything painted inside that box, so a chip poking
    // above the arch's top point (a negative offset) would otherwise get clipped away.
    <article
      ref={ref}
      className={`reveal ${inView ? 'in-view' : ''} relative w-[260px]`}
      style={{ transitionDelay: `${delayMs}ms` }}
    >
      <div className="absolute -top-5 left-4 z-10 w-11 h-11 rounded-lg bg-white shadow-soft flex flex-col items-center justify-center">
        {day !== null ? (
          <>
            <span className={`text-[8.5px] font-bold tracking-wide ${styles.chipText}`}>{month}</span>
            <span className="text-sm font-extrabold leading-none text-ink">{day}</span>
          </>
        ) : (
          <span className={`text-[9px] font-bold tracking-wide ${styles.chipText}`}>TBD</span>
        )}
      </div>

      {/*
        No fixed total height here — the photo has a fixed height, the panel below it
        sizes to its own text content, and the mask's height (via mask-size: 100% 100%)
        stretches to match whatever that adds up to. A fixed height + percentage splits
        clipped the panel text whenever the venue/dress-code lines pushed it past its
        allotted 40% (the mask crops anything outside the element's own box, same as
        overflow-hidden would).
      */}
      <div className="mask-arch shadow-card flex flex-col">
        <div
          className="texture-noise h-56 shrink-0 bg-gradient-to-br from-parchment to-sand flex items-end p-2.5"
          role="img"
          aria-label={photoLabel}
        >
          <span className="relative z-[1] text-[9.5px] uppercase tracking-wide text-ink/50">{photoLabel}</span>
          {pill && (
            <span className="absolute top-2.5 right-2.5 z-[1] bg-white/90 px-2.5 py-0.5 rounded-full text-[10px] font-semibold text-ink">
              {pill}
            </span>
          )}
        </div>

        <div className={`px-4 pt-9 pb-5 text-white relative ${styles.panel}`}>
          <h3 className="font-display font-bold text-lg mb-2">{name}</h3>
          <p className="text-[10px] uppercase tracking-wide text-white/70 mb-0.5">When</p>
          <p className="text-[12.5px] mb-2">
            {date
              ? new Date(date).toLocaleString(undefined, { dateStyle: 'medium', timeStyle: 'short' })
              : 'Date coming soon'}
          </p>
          <p className="text-[10px] uppercase tracking-wide text-white/70 mb-0.5">Where</p>
          <p className="text-[12.5px]">{venue || 'TBA'}</p>
          {dressCode && <p className="text-[11px] text-white/70 mt-2">Dress code: {dressCode}</p>}
        </div>
      </div>
    </article>
  )
}
