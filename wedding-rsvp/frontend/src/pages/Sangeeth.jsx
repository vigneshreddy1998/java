import { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { sangeethConfig } from '../config/eventConfig'
import { submitRsvp } from '../api/client'
import '../styles/sangeeth.css'

function firstName(name) {
  return name.split(' ')[0]
}

const trackTitle = `${firstName(sangeethConfig.coupleNames[0])} ❤ ${firstName(sangeethConfig.coupleNames[1])}`

export default function Sangeeth() {
  const [screen, setScreen] = useState('play')
  const [progress, setProgress] = useState(0)
  const autoAdvanceTimer = useRef(null)

  useEffect(() => {
    document.title = `Sangeeth Night — ${trackTitle}`
  }, [])

  useEffect(() => {
    if (screen !== 'track') return undefined
    setProgress(0)
    const raf = requestAnimationFrame(() => setProgress(100))
    autoAdvanceTimer.current = window.setTimeout(() => setScreen('question'), 2600)
    return () => {
      cancelAnimationFrame(raf)
      window.clearTimeout(autoAdvanceTimer.current)
    }
  }, [screen])

  return (
    <div className="sangeeth-page">
      <div className="player">
        {screen === 'play' && <PlayScreen onPlay={() => setScreen('track')} />}

        {screen === 'track' && (
          <TrackScreen progress={progress} onSkip={() => setScreen('question')} />
        )}

        {screen === 'question' && (
          <QuestionScreen onYes={() => setScreen('details')} onNo={() => setScreen('decline')} />
        )}

        {screen === 'details' && <DetailsScreen onSubmitted={() => setScreen('confirm-yes')} />}

        {screen === 'decline' && <DeclineScreen onSubmitted={() => setScreen('confirm-no')} />}

        {screen === 'confirm-yes' && (
          <ConfirmScreen
            title="Your next assignment:"
            sub="Start practicing those dance moves. 💃🕺"
          />
        )}

        {screen === 'confirm-no' && (
          <ConfirmScreen
            title=""
            sub="You'll be missed on the dance floor — thanks for letting us know."
          />
        )}

        <Link to="/" className="home-link">
          &larr; Back to the invitation
        </Link>
      </div>
    </div>
  )
}

function PlayScreen({ onPlay }) {
  return (
    <section className="screen active">
      <p className="eyebrow">Sangeeth Night</p>
      <div className="album-wrap">
        <div className="album-art" style={{ backgroundImage: `url("${sangeethConfig.photoUrl}")` }} />
        <button type="button" className="play-fab" aria-label="Press play" onClick={onPlay}>
          <span className="play-icon">▶</span>
        </button>
      </div>
      <p className="tap-hint">Press play to open your invite</p>
    </section>
  )
}

function TrackScreen({ progress, onSkip }) {
  return (
    <section className="screen active">
      <div className="album-wrap small">
        <div
          className="album-art spinning"
          style={{ backgroundImage: `url("${sangeethConfig.photoUrl}")` }}
        />
      </div>
      <p className="track-label">Track 01</p>
      <h1 className="track-title">{trackTitle}</h1>
      <p className="track-meta">Album — Sangeeth Night</p>
      <p className="track-meta">Genre — Bollywood • Tollywood • Love • Madness 😂</p>
      <div className="progress-track">
        <div className="progress-fill" style={{ width: `${progress}%` }} />
      </div>
      <button type="button" className="skip-link" onClick={onSkip}>
        Skip &rarr;
      </button>
    </section>
  )
}

function QuestionScreen({ onYes, onNo }) {
  return (
    <section className="screen active">
      <p className="question-pre">One question before we start the music…</p>
      <h2 className="question-main">Are you IN?</h2>
      <div className="question-actions">
        <button type="button" className="btn-primary" onClick={onYes}>
          ▶ YES, I&apos;M IN!
        </button>
        <button type="button" className="btn-secondary" onClick={onNo}>
          ⏸ CAN&apos;T MAKE IT
        </button>
      </div>
    </section>
  )
}

function DetailsScreen({ onSubmitted }) {
  const [name, setName] = useState('')
  const [nameInvalid, setNameInvalid] = useState(false)
  const [partySize, setPartySize] = useState(1)
  const [meal, setMeal] = useState(null)
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setNameInvalid(false)

    const trimmedName = name.trim()
    if (!trimmedName) {
      setNameInvalid(true)
      setError('Enter your name to continue.')
      return
    }
    if (!meal) {
      setError('Pick Vegetarian or Non-Vegetarian.')
      return
    }

    setSubmitting(true)
    try {
      await submitRsvp({
        eventType: sangeethConfig.eventType,
        guestName: trimmedName,
        attending: true,
        guestCount: partySize,
        mealSelections: [meal],
      })
      onSubmitted()
    } catch (err) {
      setError(err.message)
      setSubmitting(false)
    }
  }

  return (
    <section className="screen active">
      <h2 className="form-title">Lock in the details</h2>
      <form onSubmit={handleSubmit} noValidate>
        <div className="sg-field">
          <label htmlFor="sgGuestName">Your name</label>
          <input
            type="text"
            id="sgGuestName"
            placeholder="Jane Smith"
            autoComplete="name"
            className={nameInvalid ? 'sg-field-invalid' : ''}
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </div>
        <div className="sg-field">
          <label htmlFor="sgStepDown">Squad size</label>
          <div className="stepper">
            <button
              type="button"
              className="step-btn"
              id="sgStepDown"
              aria-label="Decrease"
              disabled={partySize <= 1}
              onClick={() => setPartySize((n) => Math.max(1, n - 1))}
            >
              −
            </button>
            <span className="party-size-display">{partySize}</span>
            <button
              type="button"
              className="step-btn"
              aria-label="Increase"
              disabled={partySize >= sangeethConfig.maxParty}
              onClick={() => setPartySize((n) => Math.min(sangeethConfig.maxParty, n + 1))}
            >
              +
            </button>
          </div>
        </div>
        <div className="sg-field">
          <label>Veg or Non-Veg?</label>
          <div className="chip-group">
            {sangeethConfig.mealOptions.map((option) => (
              <button
                key={option}
                type="button"
                className={`chip ${meal === option ? 'active' : ''}`}
                onClick={() => setMeal(option)}
              >
                {option}
              </button>
            ))}
          </div>
        </div>
        {error && <p className="error">{error}</p>}
        <button type="submit" className="btn-primary full" disabled={submitting}>
          {submitting ? 'Locking in…' : 'LOCK IT IN 🔒'}
        </button>
      </form>
    </section>
  )
}

function DeclineScreen({ onSubmitted }) {
  const [name, setName] = useState('')
  const [nameInvalid, setNameInvalid] = useState(false)
  const [note, setNote] = useState('')
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setNameInvalid(false)

    const trimmedName = name.trim()
    if (!trimmedName) {
      setNameInvalid(true)
      setError('Enter your name to continue.')
      return
    }

    setSubmitting(true)
    try {
      await submitRsvp({
        eventType: sangeethConfig.eventType,
        guestName: trimmedName,
        attending: false,
        message: note.trim(),
      })
      onSubmitted()
    } catch (err) {
      setError(err.message)
      setSubmitting(false)
    }
  }

  return (
    <section className="screen active">
      <h2 className="form-title">Aw, we&apos;ll miss you</h2>
      <form onSubmit={handleSubmit} noValidate>
        <div className="sg-field">
          <label htmlFor="sgDeclineName">Your name</label>
          <input
            type="text"
            id="sgDeclineName"
            placeholder="Jane Smith"
            autoComplete="name"
            className={nameInvalid ? 'sg-field-invalid' : ''}
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </div>
        <div className="sg-field">
          <label htmlFor="sgDeclineNote">Leave a note (optional)</label>
          <textarea
            id="sgDeclineNote"
            rows={3}
            placeholder="Sending love from afar..."
            value={note}
            onChange={(e) => setNote(e.target.value)}
          />
        </div>
        {error && <p className="error">{error}</p>}
        <button type="submit" className="btn-secondary full" disabled={submitting}>
          {submitting ? 'Sending…' : 'SEND ANYWAY 💌'}
        </button>
      </form>
    </section>
  )
}

function ConfirmScreen({ title, sub }) {
  return (
    <section className="screen active">
      <p className="track-label">RSVP CONFIRMED ✓</p>
      {title && <h2 className="confirm-title">{title}</h2>}
      <p className="confirm-sub">{sub}</p>
    </section>
  )
}
