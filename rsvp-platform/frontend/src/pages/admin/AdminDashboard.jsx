import { useEffect, useState } from 'react'
import { Navigate } from 'react-router-dom'
import { api } from '../../api/client.js'
import ImportTab from './ImportTab.jsx'

const TABS = ['Overview', 'Guests', 'Import', 'Songs', 'Events']

export default function AdminDashboard() {
  const token = localStorage.getItem('admin_token')
  const [tab, setTab] = useState('Overview')

  if (!token) return <Navigate to="/admin/login" replace />

  function logout() {
    localStorage.removeItem('admin_token')
    window.location.href = '/admin/login'
  }

  return (
    <div className="max-w-6xl mx-auto px-6 py-10">
      <div className="flex items-center justify-between mb-8">
        <h1 className="text-2xl font-display">Admin</h1>
        <button onClick={logout} className="text-sm text-maroon-500 underline">
          Log out
        </button>
      </div>

      <div className="flex gap-2 mb-8 overflow-x-auto">
        {TABS.map((t) => (
          <button
            key={t}
            onClick={() => setTab(t)}
            className={`px-4 py-2 rounded-lg text-sm font-medium whitespace-nowrap ${
              tab === t ? 'bg-maroon-500 text-ivory' : 'bg-white border border-gold-300/60 text-ink'
            }`}
          >
            {t}
          </button>
        ))}
      </div>

      {tab === 'Overview' && <OverviewTab token={token} />}
      {tab === 'Guests' && <GuestsTab token={token} />}
      {tab === 'Import' && <ImportTab token={token} />}
      {tab === 'Songs' && <SongsTab token={token} />}
      {tab === 'Events' && <EventsTab token={token} />}
    </div>
  )
}

function Stat({ label, value, hint }) {
  return (
    <div className="rounded-xl border border-gold-300/50 bg-white p-4">
      <div className="text-2xl font-display font-bold tabular-nums">{value}</div>
      <div className="text-xs uppercase tracking-wide text-ink/50 mt-1">{label}</div>
      {hint && <div className="text-xs text-ink/40 mt-1">{hint}</div>}
    </div>
  )
}

function OverviewTab({ token }) {
  const [data, setData] = useState(null)
  const [error, setError] = useState(null)

  useEffect(() => {
    api.adminOverview(token).then(setData).catch((e) => setError(e.message))
  }, [token])

  if (error) return <p className="text-maroon-500">{error}</p>
  if (!data) return <p className="text-ink/50">Loading...</p>

  return (
    <div className="space-y-8">
      <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
        <Stat label="Guests" value={data.totalGuests} />
        <Stat label="From your contacts" value={data.importedGuests} />
        <Stat
          label="Found the link"
          value={data.selfRegisteredGuests}
          hint={data.selfRegisteredGuests > 0 ? 'Not in your contacts' : null}
        />
      </div>

      {data.events.map((e) => (
        <div key={e.eventKey}>
          <h2 className="font-display text-lg mb-3">{e.eventName}</h2>
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
            <Stat label="Accepted" value={e.accepted} />
            <Stat label="Declined" value={e.declined} />
            <Stat label="No answer yet" value={e.pending} />
            <Stat label="Total attending" value={e.headcount} hint="Including guests they bring" />
          </div>
          {Object.keys(e.mealCounts).length > 0 && (
            <div className="mt-3 flex gap-4 text-sm text-ink/60">
              {Object.entries(e.mealCounts).map(([meal, count]) => (
                <span key={meal}>
                  {meal.replace('_', '-').toLowerCase()}: <strong className="text-ink">{count}</strong>
                </span>
              ))}
            </div>
          )}
        </div>
      ))}
    </div>
  )
}

function GuestsTab({ token }) {
  const [guests, setGuests] = useState([])
  const [events, setEvents] = useState([])
  const [search, setSearch] = useState('')
  const [error, setError] = useState(null)
  const [busyId, setBusyId] = useState(null)

  function load() {
    Promise.all([api.adminGuests(token), api.adminEvents(token)])
      .then(([g, e]) => {
        setGuests(g)
        setEvents(e)
      })
      .catch((err) => setError(err.message))
  }

  useEffect(load, [token])

  async function toggleInvite(guest, eventKey) {
    const next = guest.invitedEventKeys.includes(eventKey)
      ? guest.invitedEventKeys.filter((k) => k !== eventKey)
      : [...guest.invitedEventKeys, eventKey]
    setBusyId(guest.id)
    try {
      await api.adminUpdateInvites(token, guest.id, next)
      load()
    } catch (err) {
      setError(err.message)
    } finally {
      setBusyId(null)
    }
  }

  async function promote(guest) {
    setBusyId(guest.id)
    try {
      await api.adminPromote(token, guest.id)
      load()
    } finally {
      setBusyId(null)
    }
  }

  const filtered = guests.filter(
    (g) =>
      !search ||
      (g.name ?? '').toLowerCase().includes(search.toLowerCase()) ||
      g.phone.includes(search),
  )

  return (
    <div>
      {error && <p className="text-maroon-500 mb-4">{error}</p>}
      <input
        className="w-full max-w-sm rounded-lg border border-gold-300/60 px-3 py-2 text-sm mb-4"
        placeholder="Search by name or number"
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />

      <div className="overflow-x-auto rounded-xl border border-gold-300/50 bg-white">
        <table className="w-full text-sm">
          <thead>
            <tr className="text-left border-b border-gold-300/40">
              <th className="px-4 py-2 font-medium text-ink/60">Guest</th>
              <th className="px-4 py-2 font-medium text-ink/60">Phone</th>
              {events.map((e) => (
                <th key={e.key} className="px-3 py-2 font-medium text-ink/60 text-center">
                  {e.name.replace('The ', '')}
                </th>
              ))}
              <th className="px-4 py-2 font-medium text-ink/60">RSVPs</th>
            </tr>
          </thead>
          <tbody>
            {filtered.map((g) => (
              <tr key={g.id} className="border-b border-gold-300/20 last:border-0">
                <td className="px-4 py-2">
                  <div className="font-medium">{g.name || <span className="text-ink/40">No name</span>}</div>
                  {g.source === 'SELF_REGISTERED' && (
                    <button
                      onClick={() => promote(g)}
                      disabled={busyId === g.id}
                      className="text-[10px] uppercase tracking-wide bg-haldi-50 text-haldi-500 px-1.5 py-0.5 rounded mt-1 hover:underline"
                      title="Not in your contacts — click to add to your list"
                    >
                      Found the link ✓ add
                    </button>
                  )}
                </td>
                <td className="px-4 py-2 whitespace-nowrap text-ink/70">{g.phone}</td>
                {events.map((e) => (
                  <td key={e.key} className="px-3 py-2 text-center">
                    <input
                      type="checkbox"
                      checked={g.invitedEventKeys.includes(e.key)}
                      disabled={busyId === g.id}
                      onChange={() => toggleInvite(g, e.key)}
                      aria-label={`${g.name || g.phone} invited to ${e.name}`}
                    />
                  </td>
                ))}
                <td className="px-4 py-2 text-xs text-ink/60">
                  {g.rsvps.length === 0
                    ? '—'
                    : g.rsvps
                        .map((r) => `${r.eventKey}: ${r.status.toLowerCase()}${r.headcount > 1 ? ` (${r.headcount})` : ''}`)
                        .join(', ')}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {filtered.length === 0 && <p className="text-ink/50 text-sm mt-4">No guests yet.</p>}
    </div>
  )
}

function SongsTab({ token }) {
  const [songs, setSongs] = useState([])
  const [title, setTitle] = useState('')
  const [url, setUrl] = useState('')
  const [saving, setSaving] = useState(false)

  function load() {
    api.adminSongs(token).then(setSongs).catch(() => {})
  }
  useEffect(load, [token])

  async function addSong(e) {
    e.preventDefault()
    setSaving(true)
    try {
      await api.adminAddSong(token, { title, practiceVideoUrl: url || null })
      setTitle('')
      setUrl('')
      load()
    } finally {
      setSaving(false)
    }
  }

  const duplicates = songs.filter((s) => s.duplicate)

  return (
    <div>
      <form onSubmit={addSong} className="flex flex-wrap gap-2 mb-6">
        <input
          className="flex-1 min-w-[180px] rounded-lg border border-gold-300/60 px-3 py-2 text-sm"
          placeholder="Song title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
        />
        <input
          className="flex-1 min-w-[180px] rounded-lg border border-gold-300/60 px-3 py-2 text-sm"
          placeholder="Practice video URL (optional)"
          value={url}
          onChange={(e) => setUrl(e.target.value)}
        />
        <button disabled={saving} className="px-4 py-2 rounded-lg bg-sangeet-500 text-ivory text-sm font-medium disabled:opacity-50">
          Add
        </button>
      </form>

      {duplicates.length > 0 && (
        <div className="rounded-lg border border-haldi-400/40 bg-haldi-50 p-4 mb-6">
          <p className="text-sm font-medium text-haldi-600 mb-1">
            {duplicates.length} song{duplicates.length === 1 ? '' : 's'} picked by more than one guest
          </p>
          <p className="text-xs text-ink/60">
            Songs don't lock, so overlap is possible. Worth a word before the night.
          </p>
        </div>
      )}

      <div className="overflow-x-auto rounded-xl border border-gold-300/50 bg-white">
        <table className="w-full text-sm">
          <thead>
            <tr className="text-left border-b border-gold-300/40">
              <th className="px-4 py-2 font-medium text-ink/60">Song</th>
              <th className="px-4 py-2 font-medium text-ink/60">Picked by</th>
              <th className="px-4 py-2"></th>
            </tr>
          </thead>
          <tbody>
            {songs.map((s) => (
              <tr key={s.id} className={`border-b border-gold-300/20 last:border-0 ${s.duplicate ? 'bg-haldi-50/50' : ''}`}>
                <td className="px-4 py-2">
                  <div className="font-medium">{s.title}</div>
                  {s.practiceVideoUrl && (
                    <a href={s.practiceVideoUrl} target="_blank" rel="noreferrer" className="text-xs text-sangeet-500 underline">
                      Practice video
                    </a>
                  )}
                </td>
                <td className="px-4 py-2 text-ink/70">
                  {s.pickedBy.length === 0 ? <span className="text-ink/40">Nobody yet</span> : s.pickedBy.join(', ')}
                  {s.duplicate && <span className="ml-2 text-xs font-medium text-haldi-600">overlap</span>}
                </td>
                <td className="px-4 py-2 text-right">
                  <button
                    onClick={() => api.adminDeleteSong(token, s.id).then(load)}
                    className="text-xs text-ink/40 hover:text-maroon-500"
                  >
                    Remove
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {songs.length === 0 && <p className="text-ink/50 text-sm mt-4">No songs added yet.</p>}
    </div>
  )
}

function EventsTab({ token }) {
  const [events, setEvents] = useState([])
  const [savingKey, setSavingKey] = useState(null)
  const [savedKey, setSavedKey] = useState(null)

  useEffect(() => {
    api.adminEvents(token).then(setEvents).catch(() => {})
  }, [token])

  function update(key, field, value) {
    setEvents((prev) => prev.map((e) => (e.key === key ? { ...e, [field]: value } : e)))
  }

  async function save(event) {
    setSavingKey(event.key)
    setSavedKey(null)
    try {
      await api.adminUpdateEvent(token, event.key, {
        name: event.name,
        date: event.date || null,
        venue: event.venue,
        dressCode: event.dressCode,
        colourTheme: event.colourTheme,
      })
      setSavedKey(event.key)
    } finally {
      setSavingKey(null)
    }
  }

  return (
    <div className="space-y-4">
      {events.map((e) => (
        <div key={e.key} className="rounded-xl border border-gold-300/50 bg-white p-5">
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-display text-lg">{e.name}</h3>
            <span className="text-xs text-ink/40">
              {e.collectsRsvp ? 'Takes RSVPs' : 'Information only'}
            </span>
          </div>
          <div className="grid sm:grid-cols-2 gap-3">
            <label className="text-sm">
              <span className="block text-xs uppercase tracking-wide text-ink/50 mb-1">Date &amp; time</span>
              <input
                type="datetime-local"
                className="w-full rounded-lg border border-gold-300/60 px-3 py-2"
                value={e.date ? e.date.slice(0, 16) : ''}
                onChange={(ev) => update(e.key, 'date', ev.target.value)}
              />
            </label>
            <label className="text-sm">
              <span className="block text-xs uppercase tracking-wide text-ink/50 mb-1">Venue</span>
              <input
                className="w-full rounded-lg border border-gold-300/60 px-3 py-2"
                value={e.venue ?? ''}
                onChange={(ev) => update(e.key, 'venue', ev.target.value)}
              />
            </label>
            <label className="text-sm">
              <span className="block text-xs uppercase tracking-wide text-ink/50 mb-1">Dress code</span>
              <input
                className="w-full rounded-lg border border-gold-300/60 px-3 py-2"
                value={e.dressCode ?? ''}
                onChange={(ev) => update(e.key, 'dressCode', ev.target.value)}
              />
            </label>
            <label className="text-sm">
              <span className="block text-xs uppercase tracking-wide text-ink/50 mb-1">Colour theme</span>
              <input
                className="w-full rounded-lg border border-gold-300/60 px-3 py-2"
                placeholder="e.g. yellow and marigold"
                value={e.colourTheme ?? ''}
                onChange={(ev) => update(e.key, 'colourTheme', ev.target.value)}
              />
            </label>
          </div>
          <div className="flex items-center gap-3 mt-4">
            <button
              onClick={() => save(e)}
              disabled={savingKey === e.key}
              className="px-4 py-2 rounded-lg bg-maroon-500 text-ivory text-sm font-medium disabled:opacity-50"
            >
              {savingKey === e.key ? 'Saving...' : 'Save'}
            </button>
            {savedKey === e.key && <span className="text-sm text-sangeet-500">Saved</span>}
          </div>
        </div>
      ))}
    </div>
  )
}
