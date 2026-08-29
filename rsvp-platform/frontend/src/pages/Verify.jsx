import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useGuestSession } from '../session/GuestSession.jsx'
import { siteConfig } from '../config/siteConfig.js'

/**
 * The front door, identical for every visitor. Nothing here hints at which events exist —
 * that's only known after the server resolves the number.
 */
export default function Verify() {
  const [phone, setPhone] = useState('')
  const [name, setName] = useState('')
  const [needsName, setNeedsName] = useState(false)
  const [error, setError] = useState(null)
  const { verify, loading } = useGuestSession()
  const navigate = useNavigate()

  async function submit(e) {
    e.preventDefault()
    setError(null)
    try {
      const profile = await verify(phone, name)
      if (!profile.name) {
        // First time we've seen this number and they didn't give a name — ask once.
        setNeedsName(true)
        return
      }
      navigate('/home')
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div className="min-h-[85vh] flex items-center justify-center px-6 py-16">
      <div className="w-full max-w-md text-center">
        <p className="text-[11px] uppercase tracking-[0.14em] font-bold text-gold-500 mb-4">
          Together with our families
        </p>
        <h1 className="font-accent italic text-4xl sm:text-5xl text-maroon-500 mb-3">
          {siteConfig.coupleNames}
        </h1>
        <p className="text-ink/60 mb-10">
          {new Date(siteConfig.weddingDateFallback).toLocaleDateString(undefined, {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
          })}
          {' · '}
          {siteConfig.weddingVenueFallback}
        </p>

        <form onSubmit={submit} className="text-left space-y-4">
          <div>
            <label htmlFor="phone" className="block text-xs uppercase tracking-wide text-ink/50 mb-1.5">
              Your phone number
            </label>
            <input
              id="phone"
              type="tel"
              autoComplete="tel"
              className="w-full rounded-lg border border-gold-300/60 px-4 py-3 focus:outline-none focus:ring-2 focus:ring-maroon-400"
              placeholder="+1 704 555 0100"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              required
            />
            <p className="text-xs text-ink/45 mt-1.5">
              The number your invitation was sent to.
            </p>
          </div>

          {needsName && (
            <div>
              <label htmlFor="name" className="block text-xs uppercase tracking-wide text-ink/50 mb-1.5">
                Your name
              </label>
              <input
                id="name"
                className="w-full rounded-lg border border-gold-300/60 px-4 py-3 focus:outline-none focus:ring-2 focus:ring-maroon-400"
                placeholder="Asha Sharma"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
                autoFocus
              />
              <p className="text-xs text-ink/45 mt-1.5">We don't have a name against this number yet.</p>
            </div>
          )}

          {error && <p className="text-sm text-maroon-500">{error}</p>}

          <button
            type="submit"
            disabled={loading}
            className="w-full py-3 rounded-lg bg-maroon-500 text-ivory font-medium disabled:opacity-50"
          >
            {loading ? 'Checking...' : needsName ? 'Continue' : 'Find my invitation'}
          </button>
        </form>
      </div>
    </div>
  )
}
