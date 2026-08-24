import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import HeroSlot from '../components/HeroSlot.jsx'
import EventArchCard from '../components/EventArchCard.jsx'
import { api } from '../api/client.js'
import { siteConfig } from '../config/siteConfig.js'

export default function Home() {
  const [events, setEvents] = useState(null)
  const [code, setCode] = useState('')
  const navigate = useNavigate()

  useEffect(() => {
    api
      .getEvents()
      .then(setEvents)
      .catch(() => setEvents([])) // backend unreachable — cards fall back to config defaults below
  }, [])

  const wedding = events?.find((e) => e.type === 'WEDDING')
  const sangeet = events?.find((e) => e.type === 'SANGEET')

  function lookupInvite(e) {
    e.preventDefault()
    if (code.trim()) navigate(`/rsvp/${code.trim()}`)
  }

  return (
    <div>
      <HeroSlot coupleNames={siteConfig.coupleNames} />

      <section id="events" className="text-center pt-16 pb-2 px-6">
        <p className="text-[11px] uppercase tracking-[0.14em] font-bold text-gold-500 mb-2.5">As you scroll</p>
        <h2 className="font-accent italic text-3xl sm:text-4xl">Two nights, each on its own card</h2>
      </section>

      <div className="flex justify-center gap-6 flex-wrap px-6 py-8 pb-24">
        <EventArchCard
          name={wedding?.name || 'The Wedding'}
          date={wedding?.date || siteConfig.weddingDateFallback}
          venue={wedding?.venue || siteConfig.weddingVenueFallback}
          dressCode={wedding?.dressCode}
          accent="wed"
          photoLabel="Wedding — photo"
        />
        <EventArchCard
          name={sangeet?.name || 'Sangeet Night'}
          date={sangeet?.date || null}
          venue={sangeet?.venue || siteConfig.weddingVenueFallback}
          dressCode={sangeet?.dressCode}
          accent="sangeet"
          photoLabel="Sangeet — photo"
          delayMs={120}
        />
      </div>

      <section className="bg-parchment/60 py-14">
        <div className="max-w-md mx-auto px-6 text-center">
          <h2 className="text-2xl mb-2">Have your invite link?</h2>
          <p className="text-sm text-ink/60 mb-6">
            Enter the code from your invitation to RSVP for your family.
          </p>
          <form onSubmit={lookupInvite} className="flex gap-2">
            <input
              className="flex-1 rounded-lg border border-gold-300/60 px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-maroon-400"
              placeholder="Invite code"
              value={code}
              onChange={(e) => setCode(e.target.value)}
              aria-label="Invite code"
            />
            <button className="px-5 py-3 rounded-lg bg-maroon-500 text-ivory font-medium text-sm">
              Go
            </button>
          </form>
        </div>
      </section>

      <footer className="text-center px-6 pt-5 pb-16">
        <div className="w-14 h-px bg-gold-400 mx-auto mb-4" />
        <p className="text-xs uppercase tracking-[0.1em] text-ink/40">
          {siteConfig.coupleNames} — {new Date(siteConfig.weddingDateFallback).toLocaleDateString(undefined, {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
          })}
        </p>
      </footer>
    </div>
  )
}
