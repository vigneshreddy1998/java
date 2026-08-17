import { useEffect, useMemo, useState } from 'react'
import { fetchAdminSummary, adminExportUrl } from '../api/client'

const STORAGE_KEY = 'wedding-admin-key'

export default function Admin() {
  const [adminKey, setAdminKey] = useState(() => sessionStorage.getItem(STORAGE_KEY) || '')
  const [keyInput, setKeyInput] = useState('')
  const [summary, setSummary] = useState(null)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [eventFilter, setEventFilter] = useState('ALL')

  useEffect(() => {
    if (adminKey) load(adminKey)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  async function load(key) {
    setLoading(true)
    setError('')
    try {
      const data = await fetchAdminSummary(key)
      setSummary(data)
      sessionStorage.setItem(STORAGE_KEY, key)
      setAdminKey(key)
    } catch (err) {
      setError('Invalid admin key, or the backend is unreachable.')
      sessionStorage.removeItem(STORAGE_KEY)
      setAdminKey('')
    } finally {
      setLoading(false)
    }
  }

  if (!adminKey) {
    return (
      <div className="page" style={{ maxWidth: 420 }}>
        <h1 style={{ fontSize: '1.5rem', marginBottom: 16 }}>Admin sign-in</h1>
        <form
          onSubmit={(e) => {
            e.preventDefault()
            load(keyInput.trim())
          }}
          className="card"
        >
          <div className="field">
            <label htmlFor="adminKey">Admin key</label>
            <input
              id="adminKey"
              type="password"
              value={keyInput}
              onChange={(e) => setKeyInput(e.target.value)}
              required
            />
          </div>
          <button type="submit" className="wax-seal" disabled={loading}>
            <span className="wax-seal__label">{loading ? 'Checking…' : 'Sign in'}</span>
          </button>
        </form>
        {error && <p className="field-error" style={{ marginTop: 12 }}>{error}</p>}
      </div>
    )
  }

  const eventTypes = useMemo(() => {
    if (!summary) return []
    return [...new Set(summary.entries.map((entry) => entry.eventType || 'WEDDING'))]
  }, [summary])

  const filteredEntries = useMemo(() => {
    if (!summary) return []
    if (eventFilter === 'ALL') return summary.entries
    return summary.entries.filter((entry) => (entry.eventType || 'WEDDING') === eventFilter)
  }, [summary, eventFilter])

  const stats = useMemo(() => {
    const attending = filteredEntries.filter((e) => e.attending)
    return {
      totalResponses: filteredEntries.length,
      attendingParties: attending.length,
      totalAttendingGuests: attending.reduce((sum, e) => sum + e.guestCount, 0),
    }
  }, [filteredEntries])

  return (
    <div className="page" style={{ maxWidth: 720 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
        <h1 style={{ fontSize: '1.5rem' }}>RSVP Dashboard</h1>
        <a href={adminExportUrl()} target="_blank" rel="noreferrer">
          Export CSV
        </a>
      </div>

      {summary && (
        <>
          {eventTypes.length > 1 && (
            <div style={{ display: 'flex', gap: 8, marginBottom: 16 }}>
              {['ALL', ...eventTypes].map((type) => (
                <button
                  key={type}
                  type="button"
                  onClick={() => setEventFilter(type)}
                  style={{
                    padding: '6px 14px',
                    borderRadius: 999,
                    border: '1.5px solid var(--paper-deep)',
                    background: eventFilter === type ? 'var(--wine)' : 'var(--white)',
                    color: eventFilter === type ? 'var(--white)' : 'var(--charcoal-soft)',
                    cursor: 'pointer',
                    fontSize: '0.8rem',
                    fontWeight: 600,
                    textTransform: 'capitalize',
                  }}
                >
                  {type === 'ALL' ? 'All events' : type.toLowerCase()}
                </button>
              ))}
            </div>
          )}

          <div
            className="card"
            style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 12, marginBottom: 24 }}
          >
            <Stat label="Responses" value={stats.totalResponses} />
            <Stat label="Attending" value={stats.attendingParties} />
            <Stat label="Guests total" value={stats.totalAttendingGuests} />
          </div>

          <div className="card" style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.9rem' }}>
              <thead>
                <tr style={{ textAlign: 'left', borderBottom: '2px solid var(--paper-deep)' }}>
                  <th style={{ padding: 8 }}>Name</th>
                  <th style={{ padding: 8 }}>Event</th>
                  <th style={{ padding: 8 }}>Attending</th>
                  <th style={{ padding: 8 }}>Guests</th>
                  <th style={{ padding: 8 }}>Meals</th>
                  <th style={{ padding: 8 }}>Notes</th>
                </tr>
              </thead>
              <tbody>
                {filteredEntries.map((entry) => (
                  <tr key={entry.id} style={{ borderBottom: '1px solid var(--paper-deep)' }}>
                    <td style={{ padding: 8 }}>
                      {entry.guestName}
                      {entry.email && (
                        <div style={{ color: 'var(--charcoal-soft)', fontSize: '0.8rem' }}>{entry.email}</div>
                      )}
                    </td>
                    <td style={{ padding: 8, textTransform: 'capitalize' }}>
                      {(entry.eventType || 'WEDDING').toLowerCase()}
                    </td>
                    <td style={{ padding: 8 }}>{entry.attending ? 'Yes' : 'No'}</td>
                    <td style={{ padding: 8 }}>{entry.guestCount}</td>
                    <td style={{ padding: 8 }}>{entry.mealSelections || '—'}</td>
                    <td style={{ padding: 8 }}>{entry.allergies || entry.songRequest || entry.message || '—'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </>
      )}
    </div>
  )
}

function Stat({ label, value }) {
  return (
    <div style={{ textAlign: 'center' }}>
      <div style={{ fontFamily: 'var(--font-display)', fontSize: '1.8rem', color: 'var(--wine)' }}>{value}</div>
      <div style={{ fontSize: '0.78rem', color: 'var(--charcoal-soft)', textTransform: 'uppercase' }}>{label}</div>
    </div>
  )
}
