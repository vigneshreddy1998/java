import { useEffect, useState } from 'react'
import { Navigate } from 'react-router-dom'
import { api } from '../../api/client.js'

const TABS = ['Tracker', 'Meals', 'Songs', 'Logistics', 'Import', 'Non-responders']

export default function AdminDashboard() {
  const token = localStorage.getItem('admin_token')
  const [tab, setTab] = useState('Tracker')

  if (!token) return <Navigate to="/admin/login" replace />

  function logout() {
    localStorage.removeItem('admin_token')
    window.location.href = '/admin/login'
  }

  return (
    <div className="max-w-5xl mx-auto px-6 py-10">
      <div className="flex items-center justify-between mb-8">
        <h1 className="text-2xl font-display">Admin dashboard</h1>
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

      {tab === 'Tracker' && <TrackerTab token={token} />}
      {tab === 'Meals' && <MealsTab token={token} />}
      {tab === 'Songs' && <SongsTab token={token} />}
      {tab === 'Logistics' && <LogisticsTab token={token} />}
      {tab === 'Import' && <ImportTab token={token} />}
      {tab === 'Non-responders' && <NonRespondersTab token={token} />}
    </div>
  )
}

function Table({ columns, rows }) {
  if (rows.length === 0) return <p className="text-ink/50 text-sm">Nothing here yet.</p>
  return (
    <div className="overflow-x-auto rounded-xl border border-gold-300/50 bg-white">
      <table className="w-full text-sm">
        <thead>
          <tr className="text-left border-b border-gold-300/40">
            {columns.map((c) => (
              <th key={c} className="px-4 py-2 font-medium text-ink/60">
                {c}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((row, i) => (
            <tr key={i} className="border-b border-gold-300/20 last:border-0">
              {row.map((cell, j) => (
                <td key={j} className="px-4 py-2">
                  {cell ?? '—'}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

function TrackerTab({ token }) {
  const [eventType, setEventType] = useState('')
  const [status, setStatus] = useState('')
  const [rows, setRows] = useState([])

  useEffect(() => {
    const params = {}
    if (eventType) params.eventType = eventType
    if (status) params.status = status
    api.adminTracker(token, params).then(setRows).catch(() => setRows([]))
  }, [token, eventType, status])

  return (
    <div>
      <div className="flex gap-3 mb-4">
        <select className="rounded-lg border border-gold-300/60 px-3 py-2 text-sm" value={eventType} onChange={(e) => setEventType(e.target.value)}>
          <option value="">All events</option>
          <option value="WEDDING">Wedding</option>
          <option value="SANGEET">Sangeet</option>
        </select>
        <select className="rounded-lg border border-gold-300/60 px-3 py-2 text-sm" value={status} onChange={(e) => setStatus(e.target.value)}>
          <option value="">All statuses</option>
          <option value="ACCEPTED">Accepted</option>
          <option value="DECLINED">Declined</option>
          <option value="PENDING">Pending</option>
        </select>
      </div>
      <Table
        columns={['Family', 'Guest', 'Event', 'Status', 'Meal', 'Dietary notes', 'Plus one']}
        rows={rows.map((r) => [r.familyName, r.guestName, r.eventType, r.status, r.mealPref, r.dietaryNotes, r.plusOneName])}
      />
    </div>
  )
}

function MealsTab({ token }) {
  const [eventType, setEventType] = useState('WEDDING')
  const [summary, setSummary] = useState(null)

  useEffect(() => {
    api.adminMealSummary(token, eventType).then(setSummary).catch(() => setSummary(null))
  }, [token, eventType])

  return (
    <div>
      <select className="rounded-lg border border-gold-300/60 px-3 py-2 text-sm mb-4" value={eventType} onChange={(e) => setEventType(e.target.value)}>
        <option value="WEDDING">Wedding</option>
        <option value="SANGEET">Sangeet</option>
      </select>
      {summary && (
        <>
          <Table
            columns={['Meal preference', 'Count']}
            rows={Object.entries(summary.countsByMealPref).map(([k, v]) => [k, v])}
          />
          {summary.dietaryNotes.length > 0 && (
            <div className="mt-4 rounded-xl border border-gold-300/50 bg-white p-4">
              <h3 className="font-medium mb-2 text-sm">Dietary notes</h3>
              <ul className="text-sm space-y-1 text-ink/70 list-disc pl-5">
                {summary.dietaryNotes.map((n, i) => (
                  <li key={i}>{n}</li>
                ))}
              </ul>
            </div>
          )}
        </>
      )}
    </div>
  )
}

function SongsTab({ token }) {
  const [songs, setSongs] = useState([])
  const [title, setTitle] = useState('')
  const [url, setUrl] = useState('')
  const [saving, setSaving] = useState(false)

  function refresh() {
    api.listSongs().then(setSongs).catch(() => {})
  }
  useEffect(refresh, [])

  async function addSong(e) {
    e.preventDefault()
    setSaving(true)
    try {
      await api.adminCreateSong(token, { title, practiceVideoUrl: url || null })
      setTitle('')
      setUrl('')
      refresh()
    } finally {
      setSaving(false)
    }
  }

  return (
    <div>
      <form onSubmit={addSong} className="flex gap-2 mb-6">
        <input
          className="flex-1 rounded-lg border border-gold-300/60 px-3 py-2 text-sm"
          placeholder="Song title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
        />
        <input
          className="flex-1 rounded-lg border border-gold-300/60 px-3 py-2 text-sm"
          placeholder="Practice video URL (optional)"
          value={url}
          onChange={(e) => setUrl(e.target.value)}
        />
        <button disabled={saving} className="px-4 py-2 rounded-lg bg-maroon-500 text-ivory text-sm font-medium disabled:opacity-50">
          Add
        </button>
      </form>
      <Table
        columns={['Title', 'Status', 'Claimed by']}
        rows={songs.map((s) => [s.title, s.locked ? 'Taken' : 'Available', s.claimedByFamilyName])}
      />
    </div>
  )
}

function LogisticsTab({ token }) {
  const [rows, setRows] = useState([])
  useEffect(() => {
    api.adminLogistics(token).then(setRows).catch(() => setRows([]))
  }, [token])

  return (
    <Table
      columns={['Family', 'Guest', 'Flight #', 'Arrival', 'Airport', 'Pickup needed']}
      rows={rows.map((r) => [
        r.familyName,
        r.guestName,
        r.flightNumber,
        r.arrivalDatetime ? new Date(r.arrivalDatetime).toLocaleString() : null,
        r.airport,
        r.pickupNeeded === null ? null : r.pickupNeeded ? 'Yes' : 'No',
      ])}
    />
  )
}

function ImportTab({ token }) {
  const [file, setFile] = useState(null)
  const [results, setResults] = useState(null)
  const [uploading, setUploading] = useState(false)

  async function upload(e) {
    e.preventDefault()
    if (!file) return
    setUploading(true)
    try {
      const res = await api.adminImportGuests(token, file)
      setResults(res)
    } finally {
      setUploading(false)
    }
  }

  return (
    <div>
      <p className="text-sm text-ink/60 mb-4">
        CSV columns: <code>family_name, guest_name, meal_pref, language_pref</code> (meal_pref and
        language_pref optional). Guests sharing a family name are grouped under one invite link.
      </p>
      <form onSubmit={upload} className="flex gap-2 mb-6">
        <input type="file" accept=".csv" onChange={(e) => setFile(e.target.files[0])} className="text-sm" />
        <button disabled={uploading || !file} className="px-4 py-2 rounded-lg bg-maroon-500 text-ivory text-sm font-medium disabled:opacity-50">
          {uploading ? 'Uploading...' : 'Import'}
        </button>
      </form>
      {results && (
        <Table
          columns={['Family', 'Invite link']}
          rows={results.map((r) => [r.familyName, r.inviteLink])}
        />
      )}
    </div>
  )
}

function NonRespondersTab({ token }) {
  const [rows, setRows] = useState([])
  const [sending, setSending] = useState(false)
  const [sentCount, setSentCount] = useState(null)

  function refresh() {
    api.adminNonResponders(token).then(setRows).catch(() => setRows([]))
  }
  useEffect(refresh, [token])

  async function sendReminders() {
    setSending(true)
    try {
      const res = await api.adminSendReminders(token)
      setSentCount(res.remindersQueued)
    } finally {
      setSending(false)
    }
  }

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <p className="text-sm text-ink/60">{rows.length} outstanding RSVP(s)</p>
        <button
          onClick={sendReminders}
          disabled={sending}
          className="px-4 py-2 rounded-lg bg-maroon-500 text-ivory text-sm font-medium disabled:opacity-50"
        >
          {sending ? 'Sending...' : 'Send reminders'}
        </button>
      </div>
      {sentCount !== null && (
        <p className="text-sm text-gold-500 mb-4">
          Queued {sentCount} reminder(s). (No messaging channel is wired up yet — this logs the batch server-side.)
        </p>
      )}
      <Table
        columns={['Family', 'Guest', 'Event']}
        rows={rows.map((r) => [r.familyName, r.guestName, r.eventType])}
      />
    </div>
  )
}
