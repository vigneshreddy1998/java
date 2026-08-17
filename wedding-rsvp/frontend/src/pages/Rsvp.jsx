import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { eventConfig } from '../config/eventConfig'
import { lookupGuestByCode, submitRsvp } from '../api/client'
import WaxSeal from '../components/WaxSeal'
import '../styles/rsvp.css'

const emptyForm = {
  guestId: null,
  guestName: '',
  email: '',
  attending: null,
  guestCount: 1,
  mealSelections: [],
  allergies: '',
  songRequest: '',
  message: '',
}

export default function Rsvp() {
  const navigate = useNavigate()
  const [codeInput, setCodeInput] = useState('')
  const [codeError, setCodeError] = useState('')
  const [maxGuests, setMaxGuests] = useState(6)
  const [unlocked, setUnlocked] = useState(!eventConfig.requireInviteCode)
  const [form, setForm] = useState(emptyForm)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState('')

  async function handleCodeSubmit(e) {
    e.preventDefault()
    setCodeError('')
    try {
      const guest = await lookupGuestByCode(codeInput.trim())
      setForm((f) => ({
        ...f,
        guestId: guest.guestId,
        guestName: guest.partyName,
        email: guest.email || '',
      }))
      setMaxGuests(guest.maxGuests)
      setUnlocked(true)
    } catch (err) {
      setCodeError(err.message)
    }
  }

  function updateField(field, value) {
    setForm((f) => ({ ...f, [field]: value }))
  }

  function setGuestCount(count) {
    setForm((f) => ({
      ...f,
      guestCount: count,
      mealSelections: Array.from({ length: count }, (_, i) => f.mealSelections[i] || ''),
    }))
  }

  function setMeal(index, value) {
    setForm((f) => {
      const meals = [...f.mealSelections]
      meals[index] = value
      return { ...f, mealSelections: meals }
    })
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')

    if (form.attending === null) {
      setError("Please let us know if you'll be attending.")
      return
    }
    if (!form.guestName.trim() || !form.email.trim()) {
      setError('Name and email are required.')
      return
    }

    setSubmitting(true)
    try {
      await submitRsvp({
        guestId: form.guestId,
        guestName: form.guestName.trim(),
        email: form.email.trim(),
        attending: form.attending,
        guestCount: form.attending ? form.guestCount : 0,
        mealSelections: form.attending ? form.mealSelections.filter(Boolean) : [],
        allergies: form.allergies,
        songRequest: form.songRequest,
        message: form.message,
      })
      navigate('/thank-you', { state: { attending: form.attending } })
    } catch (err) {
      setError(err.message)
    } finally {
      setSubmitting(false)
    }
  }

  if (!unlocked) {
    return (
      <div className="page">
        <Link to="/" className="back-link">
          &larr; Back
        </Link>
        <div className="rsvp-header">
          <p className="eyebrow">RSVP</p>
          <h1>Find your invitation</h1>
          <p>Enter the code from your invite to get started.</p>
        </div>
        <form className="card code-form" onSubmit={handleCodeSubmit}>
          <input
            value={codeInput}
            onChange={(e) => setCodeInput(e.target.value)}
            placeholder="Invite code"
            aria-label="Invite code"
            required
          />
          <WazealSubmit />
        </form>
        {codeError && <p className="field-error" style={{ marginTop: 10 }}>{codeError}</p>}
      </div>
    )
  }

  return (
    <div className="page">
      <Link to="/" className="back-link">
        &larr; Back
      </Link>
      <div className="rsvp-header">
        <p className="eyebrow">RSVP</p>
        <h1>{form.guestName ? `Hi, ${form.guestName}` : 'Reserve your seat'}</h1>
        <p>Please respond by {eventConfig.rsvpDeadline}.</p>
      </div>

      {error && <div className="banner banner--error">{error}</div>}

      <form onSubmit={handleSubmit} className="card">
        <div className="field">
          <label htmlFor="guestName">Full name</label>
          <input
            id="guestName"
            value={form.guestName}
            onChange={(e) => updateField('guestName', e.target.value)}
            required
          />
        </div>

        <div className="field">
          <label htmlFor="email">Email</label>
          <input
            id="email"
            type="email"
            value={form.email}
            onChange={(e) => updateField('email', e.target.value)}
            required
          />
        </div>

        <div className="attend-toggle">
          <button
            type="button"
            className={form.attending === true ? 'active-yes' : ''}
            onClick={() => updateField('attending', true)}
          >
            Joyfully Accepts
          </button>
          <button
            type="button"
            className={form.attending === false ? 'active-no' : ''}
            onClick={() => updateField('attending', false)}
          >
            Regretfully Declines
          </button>
        </div>

        {form.attending && (
          <>
            <div className="field">
              <label htmlFor="guestCount">Number attending (including you)</label>
              <select
                id="guestCount"
                value={form.guestCount}
                onChange={(e) => setGuestCount(Number(e.target.value))}
              >
                {Array.from({ length: maxGuests }, (_, i) => i + 1).map((n) => (
                  <option key={n} value={n}>
                    {n}
                  </option>
                ))}
              </select>
            </div>

            {Array.from({ length: form.guestCount }, (_, i) => (
              <div className="field" key={i}>
                <label>Meal choice &mdash; guest {i + 1}</label>
                <div className="meal-row">
                  <select value={form.mealSelections[i] || ''} onChange={(e) => setMeal(i, e.target.value)}>
                    <option value="">Select a meal</option>
                    {eventConfig.mealOptions.map((meal) => (
                      <option key={meal} value={meal}>
                        {meal}
                      </option>
                    ))}
                  </select>
                </div>
              </div>
            ))}

            <div className="field">
              <label htmlFor="allergies">Dietary restrictions or allergies</label>
              <textarea
                id="allergies"
                rows={2}
                value={form.allergies}
                onChange={(e) => updateField('allergies', e.target.value)}
              />
            </div>

            <div className="field">
              <label htmlFor="songRequest">Song request</label>
              <input
                id="songRequest"
                value={form.songRequest}
                onChange={(e) => updateField('songRequest', e.target.value)}
              />
            </div>
          </>
        )}

        <div className="field">
          <label htmlFor="message">A note for the couple (optional)</label>
          <textarea
            id="message"
            rows={3}
            value={form.message}
            onChange={(e) => updateField('message', e.target.value)}
          />
        </div>

        <div className="submit-row">
          <WaxSeal type="submit" size="lg" disabled={submitting}>
            {submitting ? 'Sending…' : 'Seal My RSVP'}
          </WaxSeal>
        </div>
      </form>
    </div>
  )
}

function WazealSubmit() {
  return (
    <button type="submit" className="wax-seal">
      <span className="wax-seal__label">Find Invite</span>
    </button>
  )
}
