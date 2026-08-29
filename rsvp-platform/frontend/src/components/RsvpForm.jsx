import { useEffect, useState } from 'react'
import { api } from '../api/client.js'

const MEALS = [
  ['VEG', 'Vegetarian'],
  ['NON_VEG', 'Non-vegetarian'],
]

const BUTTON_ACCENT = {
  wed: 'bg-maroon-500 border-maroon-500',
  sangeet: 'bg-sangeet-500 border-sangeet-500',
  haldi: 'bg-haldi-500 border-haldi-500',
  engagement: 'bg-engagement-500 border-engagement-500',
}

export default function RsvpForm({ event, existing, songs, songsError, onSaved }) {
  const [status, setStatus] = useState(existing?.status ?? null)
  const [headcount, setHeadcount] = useState(existing?.headcount || 1)
  const [mealPref, setMealPref] = useState(existing?.mealPref ?? '')
  const [dietaryNotes, setDietaryNotes] = useState(existing?.dietaryNotes ?? '')
  const [companions, setCompanions] = useState(existing?.companions ?? [])
  const [songIds, setSongIds] = useState([])
  const [songsReady, setSongsReady] = useState(false)
  const [consent, setConsent] = useState(true)

  // The song list arrives after first render, so the guest's existing picks can't be seeded
  // in useState — that initialiser only runs once, against an empty list. Without this the
  // form would show a returning guest's picks as unchecked and wipe them on save.
  useEffect(() => {
    if (songsReady || songs.length === 0) return
    setSongIds(songs.filter((s) => s.pickedByMe).map((s) => s.id))
    setSongsReady(true)
  }, [songs, songsReady])
  const [saving, setSaving] = useState(false)
  const [saved, setSaved] = useState(false)
  const [error, setError] = useState(null)

  const accentClass = BUTTON_ACCENT[event.accent] ?? BUTTON_ACCENT.wed

  function toggleSong(id) {
    setSongIds((prev) => (prev.includes(id) ? prev.filter((s) => s !== id) : [...prev, id]))
  }

  function updateCompanion(index, field, value) {
    setCompanions((prev) => prev.map((c, i) => (i === index ? { ...c, [field]: value } : c)))
  }

  async function submit(e) {
    e.preventDefault()
    if (!status) return
    setSaving(true)
    setError(null)
    try {
      await api.submitRsvp(event.key, {
        status,
        headcount: status === 'ACCEPTED' ? Number(headcount) : 0,
        mealPref: mealPref || null,
        dietaryNotes: dietaryNotes || null,
        companions: companions.filter((c) => c.name || c.phone),
        songIds,
        whatsappConsent: consent,
      })
      await onSaved()
      setSaved(true)
    } catch (err) {
      setError(err.message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <form onSubmit={submit} className="rounded-xl border border-gold-300/50 bg-white p-6 space-y-6">
      {existing && !saved && (
        <p className="text-xs uppercase tracking-wide text-ink/45">
          You answered already — change anything below and save again.
        </p>
      )}

      <div>
        <p className="text-sm font-medium mb-3">Will you be joining us?</p>
        <div className="flex gap-2">
          <button
            type="button"
            onClick={() => setStatus('ACCEPTED')}
            className={`flex-1 py-2.5 rounded-lg text-sm font-medium border transition-colors ${
              status === 'ACCEPTED' ? `${accentClass} text-ivory` : 'bg-white border-gold-300/60 text-ink'
            }`}
          >
            Yes, count me in
          </button>
          <button
            type="button"
            onClick={() => setStatus('DECLINED')}
            className={`flex-1 py-2.5 rounded-lg text-sm font-medium border transition-colors ${
              status === 'DECLINED' ? 'bg-ink border-ink text-ivory' : 'bg-white border-gold-300/60 text-ink'
            }`}
          >
            Sorry, can't make it
          </button>
        </div>
      </div>

      {status === 'ACCEPTED' && (
        <>
          <div>
            <label htmlFor="headcount" className="block text-xs uppercase tracking-wide text-ink/50 mb-1.5">
              How many attending, including you?
            </label>
            <input
              id="headcount"
              type="number"
              min="1"
              max="20"
              className="w-24 rounded-lg border border-gold-300/60 px-3 py-2"
              value={headcount}
              onChange={(e) => setHeadcount(e.target.value)}
            />
          </div>

          {Number(headcount) > 1 && (
            <div>
              <p className="text-xs uppercase tracking-wide text-ink/50 mb-1.5">
                Who's coming with you? <span className="normal-case tracking-normal">(optional)</span>
              </p>
              <div className="space-y-2">
                {companions.map((c, i) => (
                  <div key={i} className="grid grid-cols-2 gap-2">
                    <input
                      className="rounded-lg border border-gold-300/60 px-3 py-2 text-sm"
                      placeholder="Name"
                      value={c.name ?? ''}
                      onChange={(e) => updateCompanion(i, 'name', e.target.value)}
                    />
                    <input
                      className="rounded-lg border border-gold-300/60 px-3 py-2 text-sm"
                      placeholder="Phone (for updates)"
                      value={c.phone ?? ''}
                      onChange={(e) => updateCompanion(i, 'phone', e.target.value)}
                    />
                  </div>
                ))}
              </div>
              <button
                type="button"
                onClick={() => setCompanions((prev) => [...prev, { name: '', phone: '' }])}
                className="text-xs text-ink/60 underline mt-2"
              >
                + Add someone
              </button>
            </div>
          )}

          {event.collectsMeal && (
            <>
              <div>
                <label htmlFor="meal" className="block text-xs uppercase tracking-wide text-ink/50 mb-1.5">
                  Meal preference
                </label>
                <select
                  id="meal"
                  className="w-full rounded-lg border border-gold-300/60 px-3 py-2 text-sm"
                  value={mealPref}
                  onChange={(e) => setMealPref(e.target.value)}
                >
                  <option value="">Select...</option>
                  {MEALS.map(([value, label]) => (
                    <option key={value} value={value}>
                      {label}
                    </option>
                  ))}
                </select>
              </div>
              <div>
                <label htmlFor="diet" className="block text-xs uppercase tracking-wide text-ink/50 mb-1.5">
                  Allergies or dietary notes
                </label>
                <input
                  id="diet"
                  className="w-full rounded-lg border border-gold-300/60 px-3 py-2 text-sm"
                  placeholder="Anything the kitchen should know"
                  value={dietaryNotes}
                  onChange={(e) => setDietaryNotes(e.target.value)}
                />
              </div>
            </>
          )}

          {event.collectsSongs && (
            <div>
              <p className="text-sm font-medium mb-1">Pick songs to perform</p>
              <p className="text-xs text-ink/55 mb-3">
                Choose as many as you like. Others may pick the same song — we'll sort out any overlap.
              </p>
              {songsError && <p className="text-sm text-maroon-500 mb-2">{songsError}</p>}
              {songs.length === 0 ? (
                <p className="text-sm text-ink/50">No songs added yet — check back soon.</p>
              ) : (
                <div className="space-y-2">
                  {songs.map((song) => (
                    <label
                      key={song.id}
                      className="flex items-start gap-3 rounded-lg border border-gold-300/50 p-3 cursor-pointer hover:bg-parchment/40 transition-colors"
                    >
                      <input
                        type="checkbox"
                        className="mt-1"
                        checked={songIds.includes(song.id)}
                        onChange={() => toggleSong(song.id)}
                      />
                      <span className="flex-1">
                        <span className="block text-sm font-medium">{song.title}</span>
                        {song.practiceVideoUrl && (
                          <a
                            href={song.practiceVideoUrl}
                            target="_blank"
                            rel="noreferrer"
                            onClick={(e) => e.stopPropagation()}
                            className="text-xs text-sangeet-500 underline"
                          >
                            Practice video
                          </a>
                        )}
                      </span>
                    </label>
                  ))}
                </div>
              )}
            </div>
          )}

          <label className="flex items-start gap-2.5 text-sm text-ink/70">
            <input
              type="checkbox"
              className="mt-1"
              checked={consent}
              onChange={(e) => setConsent(e.target.checked)}
            />
            <span>Send me wedding updates on WhatsApp — travel details, timings, the livestream link.</span>
          </label>
        </>
      )}

      {error && <p className="text-sm text-maroon-500">{error}</p>}
      {saved && <p className="text-sm text-sangeet-500 font-medium">Saved. Thank you!</p>}

      <button
        type="submit"
        disabled={saving || !status}
        className="w-full py-3 rounded-lg bg-gold-400 text-ink font-medium disabled:opacity-50"
      >
        {saving ? 'Saving...' : existing ? 'Update my answer' : 'Send RSVP'}
      </button>
    </form>
  )
}
