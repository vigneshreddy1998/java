import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { api } from '../api/client.js'
import ChatWidget from '../components/ChatWidget.jsx'

const MEAL_OPTIONS = ['VEG', 'NON_VEG', 'VEGAN', 'JAIN', 'OTHER']

export default function Wedding() {
  const { inviteToken } = useParams()
  const [family, setFamily] = useState(null)
  const [event, setEvent] = useState(null)
  const [error, setError] = useState(null)
  const [savingGuestId, setSavingGuestId] = useState(null)
  const [chatGuestId, setChatGuestId] = useState(null)

  useEffect(() => {
    if (!inviteToken) return
    api.getFamily(inviteToken).then(setFamily).catch((e) => setError(e.message))
    api
      .getEvents()
      .then((events) => setEvent(events.find((e) => e.type === 'WEDDING')))
      .catch(() => {})
  }, [inviteToken])

  async function handleSubmit(guest, formValues) {
    setSavingGuestId(guest.id)
    try {
      await api.submitRsvp({
        guestId: guest.id,
        eventType: 'WEDDING',
        status: formValues.status,
        mealPref: formValues.mealPref || null,
        dietaryNotes: formValues.dietaryNotes || null,
        plusOneName: formValues.plusOneName || null,
        plusOneMealPref: formValues.plusOneMealPref || null,
      })
      const refreshed = await api.getFamily(inviteToken)
      setFamily(refreshed)
      if (formValues.status === 'ACCEPTED') setChatGuestId(guest.id)
    } catch (e) {
      setError(e.message)
    } finally {
      setSavingGuestId(null)
    }
  }

  if (!inviteToken) {
    return (
      <div className="max-w-2xl mx-auto px-6 py-20 text-center">
        <p>You'll need your invite link to RSVP. Check your invitation for the link, or enter your code on the home page.</p>
      </div>
    )
  }

  if (error) {
    return <div className="max-w-2xl mx-auto px-6 py-20 text-center text-maroon-500">{error}</div>
  }

  if (!family) {
    return <div className="max-w-2xl mx-auto px-6 py-20 text-center text-ink/50">Loading...</div>
  }

  return (
    <div className="max-w-2xl mx-auto px-6 py-16">
      <h1 className="text-4xl font-display text-maroon-500 text-center mb-2">{event?.name || 'The Wedding'}</h1>
      {event?.venue && <p className="text-center text-ink/60 mb-1">{event.venue}</p>}
      {event?.date && (
        <p className="text-center text-ink/60 mb-1">
          {new Date(event.date).toLocaleString(undefined, { dateStyle: 'full', timeStyle: 'short' })}
        </p>
      )}
      {event?.dressCode && <p className="text-center text-ink/50 text-sm mb-10">Dress code: {event.dressCode}</p>}

      <p className="text-center text-ink/70 mb-10">Hi, {family.displayName}! Please RSVP for each guest below.</p>

      <div className="space-y-6">
        {family.guests.map((guest) => (
          <GuestRsvpCard
            key={guest.id}
            guest={guest}
            saving={savingGuestId === guest.id}
            onSubmit={(values) => handleSubmit(guest, values)}
          />
        ))}
      </div>

      {chatGuestId && <ChatWidget guestId={chatGuestId} onClose={() => setChatGuestId(null)} />}
    </div>
  )
}

function GuestRsvpCard({ guest, saving, onSubmit }) {
  const existing = guest.rsvps.find((r) => r.eventType === 'WEDDING')
  const [status, setStatus] = useState(existing?.status || 'PENDING')
  const [mealPref, setMealPref] = useState(guest.mealPref || '')
  const [dietaryNotes, setDietaryNotes] = useState(guest.dietaryNotes || '')
  const [plusOneName, setPlusOneName] = useState(existing?.plusOneName || '')
  const [plusOneMealPref, setPlusOneMealPref] = useState(existing?.plusOneMealPref || '')

  function submit(e) {
    e.preventDefault()
    onSubmit({ status, mealPref, dietaryNotes, plusOneName, plusOneMealPref })
  }

  return (
    <form onSubmit={submit} className="rounded-xl border border-gold-300/50 bg-white p-5 space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="font-display text-lg">{guest.name}</h3>
        {existing?.status && existing.status !== 'PENDING' && (
          <span className="text-xs uppercase tracking-wide text-ink/50">Current: {existing.status}</span>
        )}
      </div>

      <div className="flex gap-2">
        {['ACCEPTED', 'DECLINED'].map((opt) => (
          <button
            type="button"
            key={opt}
            onClick={() => setStatus(opt)}
            className={`flex-1 py-2 rounded-lg text-sm font-medium border ${
              status === opt
                ? 'bg-maroon-500 text-ivory border-maroon-500'
                : 'bg-white text-ink border-gold-300/60'
            }`}
          >
            {opt === 'ACCEPTED' ? "I'll be there" : "Can't make it"}
          </button>
        ))}
      </div>

      {status === 'ACCEPTED' && (
        <>
          <div>
            <label className="block text-xs uppercase tracking-wide text-ink/50 mb-1">Meal preference</label>
            <select
              className="w-full rounded-lg border border-gold-300/60 px-3 py-2 text-sm"
              value={mealPref}
              onChange={(e) => setMealPref(e.target.value)}
            >
              <option value="">Select...</option>
              {MEAL_OPTIONS.map((m) => (
                <option key={m} value={m}>
                  {m.replace('_', ' ')}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-xs uppercase tracking-wide text-ink/50 mb-1">Dietary notes</label>
            <input
              className="w-full rounded-lg border border-gold-300/60 px-3 py-2 text-sm"
              placeholder="Allergies, etc. (optional)"
              value={dietaryNotes}
              onChange={(e) => setDietaryNotes(e.target.value)}
            />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs uppercase tracking-wide text-ink/50 mb-1">Plus one (optional)</label>
              <input
                className="w-full rounded-lg border border-gold-300/60 px-3 py-2 text-sm"
                placeholder="Guest name"
                value={plusOneName}
                onChange={(e) => setPlusOneName(e.target.value)}
              />
            </div>
            <div>
              <label className="block text-xs uppercase tracking-wide text-ink/50 mb-1">Their meal</label>
              <select
                className="w-full rounded-lg border border-gold-300/60 px-3 py-2 text-sm"
                value={plusOneMealPref}
                onChange={(e) => setPlusOneMealPref(e.target.value)}
                disabled={!plusOneName}
              >
                <option value="">Select...</option>
                {MEAL_OPTIONS.map((m) => (
                  <option key={m} value={m}>
                    {m.replace('_', ' ')}
                  </option>
                ))}
              </select>
            </div>
          </div>
        </>
      )}

      <button
        type="submit"
        disabled={saving || status === 'PENDING'}
        className="w-full py-2 rounded-lg bg-gold-400 text-ink font-medium text-sm disabled:opacity-50"
      >
        {saving ? 'Saving...' : 'Save RSVP'}
      </button>
    </form>
  )
}
