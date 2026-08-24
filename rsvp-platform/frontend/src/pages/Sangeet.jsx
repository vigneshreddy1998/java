import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { api } from '../api/client.js'
import SongCard from '../components/SongCard.jsx'

export default function Sangeet() {
  const { inviteToken } = useParams()
  const [family, setFamily] = useState(null)
  const [event, setEvent] = useState(null)
  const [songs, setSongs] = useState([])
  const [error, setError] = useState(null)
  const [saving, setSaving] = useState(false)
  const [claimingId, setClaimingId] = useState(null)
  const [songError, setSongError] = useState(null)

  useEffect(() => {
    if (!inviteToken) return
    api.getFamily(inviteToken).then(setFamily).catch((e) => setError(e.message))
    api
      .getEvents()
      .then((events) => setEvent(events.find((e) => e.type === 'SANGEET')))
      .catch(() => {})
    refreshSongs()
  }, [inviteToken])

  function refreshSongs() {
    api.listSongs().then(setSongs).catch(() => {})
  }

  // Poll for live "taken" status so guests see claims from other families without refreshing.
  useEffect(() => {
    const id = setInterval(refreshSongs, 8000)
    return () => clearInterval(id)
  }, [])

  async function handleRsvp(guest, status) {
    setSaving(true)
    try {
      await api.submitRsvp({ guestId: guest.id, eventType: 'SANGEET', status })
      const refreshed = await api.getFamily(inviteToken)
      setFamily(refreshed)
    } catch (e) {
      setError(e.message)
    } finally {
      setSaving(false)
    }
  }

  async function handleClaim(songId) {
    setClaimingId(songId)
    setSongError(null)
    try {
      await api.claimSong(songId, inviteToken)
      refreshSongs()
    } catch (e) {
      setSongError(e.message)
      refreshSongs()
    } finally {
      setClaimingId(null)
    }
  }

  if (!inviteToken) {
    return (
      <div className="max-w-2xl mx-auto px-6 py-20 text-center">
        <p>You'll need your invite link to RSVP. Check your invitation, or enter your code on the home page.</p>
      </div>
    )
  }

  return (
    <div className="max-w-4xl mx-auto px-6 py-16">
      <h1 className="text-4xl font-display text-sangeet-500 text-center mb-2">{event?.name || 'Sangeet Night'}</h1>
      {event?.venue && <p className="text-center text-ink/60 mb-1">{event.venue}</p>}
      {event?.date && (
        <p className="text-center text-ink/60 mb-1">
          {new Date(event.date).toLocaleString(undefined, { dateStyle: 'full', timeStyle: 'short' })}
        </p>
      )}
      {event?.dressCode && <p className="text-center text-ink/50 text-sm mb-10">Dress code: {event.dressCode}</p>}

      {error && <p className="text-center text-maroon-500 mb-6">{error}</p>}

      {family && (
        <div className="max-w-md mx-auto mb-16 space-y-3">
          {family.guests.map((guest) => {
            const existing = guest.rsvps.find((r) => r.eventType === 'SANGEET')
            return (
              <div key={guest.id} className="rounded-xl border border-gold-300/50 bg-white p-4 flex items-center justify-between">
                <span className="font-medium">{guest.name}</span>
                <div className="flex gap-2">
                  <button
                    disabled={saving}
                    onClick={() => handleRsvp(guest, 'ACCEPTED')}
                    className={`px-3 py-1.5 rounded-lg text-xs font-medium border ${
                      existing?.status === 'ACCEPTED'
                        ? 'bg-sangeet-500 text-ivory border-sangeet-500'
                        : 'border-gold-300/60'
                    }`}
                  >
                    In
                  </button>
                  <button
                    disabled={saving}
                    onClick={() => handleRsvp(guest, 'DECLINED')}
                    className={`px-3 py-1.5 rounded-lg text-xs font-medium border ${
                      existing?.status === 'DECLINED'
                        ? 'bg-ink text-ivory border-ink'
                        : 'border-gold-300/60'
                    }`}
                  >
                    Can't make it
                  </button>
                </div>
              </div>
            )
          })}
        </div>
      )}

      <section>
        <h2 className="text-2xl font-display text-sangeet-500 text-center mb-2">Claim a performance song</h2>
        <p className="text-center text-ink/60 text-sm mb-8">
          Pick a song to perform &mdash; once it's claimed, it's locked in for that family.
        </p>
        {songError && <p className="text-center text-maroon-500 text-sm mb-4">{songError}</p>}
        {songs.length === 0 ? (
          <p className="text-center text-ink/50">No songs uploaded yet — check back soon!</p>
        ) : (
          <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {songs.map((song) => (
              <SongCard key={song.id} song={song} claiming={claimingId === song.id} onClaim={handleClaim} />
            ))}
          </div>
        )}
      </section>
    </div>
  )
}
