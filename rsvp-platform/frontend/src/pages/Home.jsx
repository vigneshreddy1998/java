import EventArchCard from '../components/EventArchCard.jsx'
import HeroSlot from '../components/HeroSlot.jsx'
import { useGuestSession } from '../session/GuestSession.jsx'
import { siteConfig } from '../config/siteConfig.js'

export default function Home() {
  const { me, events, rsvpFor } = useGuestSession()

  const outstanding = events.filter((e) => e.collectsRsvp && !rsvpFor(e.key))

  return (
    <div>
      <HeroSlot coupleNames={siteConfig.coupleNames} guestName={me?.name} />

      <section id="events" className="text-center pt-16 pb-2 px-6">
        <p className="text-[11px] uppercase tracking-[0.14em] font-bold text-gold-500 mb-2.5">
          The celebrations
        </p>
        <h2 className="font-accent italic text-3xl sm:text-4xl">
          {events.length === 1 ? 'One night to remember' : `${events.length} events, one week`}
        </h2>
        {outstanding.length > 0 && (
          <p className="text-sm text-ink/60 mt-4">
            {outstanding.length === 1
              ? `We still need your answer for the ${outstanding[0].name}.`
              : `We still need your answer for ${outstanding.length} of them.`}
          </p>
        )}
      </section>

      <div className="flex justify-center gap-6 flex-wrap px-6 py-10 pb-24">
        {events.map((event, i) => (
          <EventArchCard key={event.key} event={event} rsvp={rsvpFor(event.key)} delayMs={i * 90} />
        ))}
      </div>

      <footer className="text-center px-6 pt-5 pb-16 border-t border-gold-300/30">
        <div className="w-14 h-px bg-gold-400 mx-auto mb-4" />
        <p className="text-xs uppercase tracking-[0.1em] text-ink/40">
          {siteConfig.coupleNames} —{' '}
          {new Date(siteConfig.weddingDateFallback).toLocaleDateString(undefined, {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
          })}
        </p>
      </footer>
    </div>
  )
}
