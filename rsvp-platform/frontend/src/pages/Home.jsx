import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Countdown from '../components/Countdown.jsx'
import { api } from '../api/client.js'
import { siteConfig } from '../config/siteConfig.js'

export default function Home() {
  const [weddingEvent, setWeddingEvent] = useState(null)
  const [code, setCode] = useState('')
  const navigate = useNavigate()

  useEffect(() => {
    api
      .getEvents()
      .then((events) => setWeddingEvent(events.find((e) => e.type === 'WEDDING')))
      .catch(() => setWeddingEvent(null))
  }, [])

  const targetDate = weddingEvent?.date || siteConfig.weddingDateFallback

  function lookupInvite(e) {
    e.preventDefault()
    if (code.trim()) navigate(`/rsvp/${code.trim()}`)
  }

  return (
    <div>
      <section className="max-w-3xl mx-auto text-center px-6 pt-20 pb-16">
        <p className="uppercase tracking-[0.3em] text-gold-500 text-xs mb-4">Together with our families</p>
        <h1 className="text-4xl sm:text-6xl font-display text-maroon-500 mb-6">{siteConfig.coupleNames}</h1>
        <p className="text-ink/70 max-w-xl mx-auto mb-10">{siteConfig.story}</p>

        <Countdown targetDate={targetDate} />

        {weddingEvent?.venue && (
          <p className="mt-10 text-sm text-ink/60">
            {weddingEvent.venue}
            {weddingEvent.date && ` · ${new Date(weddingEvent.date).toLocaleDateString(undefined, {
              year: 'numeric',
              month: 'long',
              day: 'numeric',
            })}`}
          </p>
        )}
      </section>

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
            />
            <button className="px-5 py-3 rounded-lg bg-maroon-500 text-ivory font-medium text-sm">
              Go
            </button>
          </form>
        </div>
      </section>
    </div>
  )
}
