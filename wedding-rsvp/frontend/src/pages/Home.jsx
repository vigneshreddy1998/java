import { useState } from 'react'
import { Link } from 'react-router-dom'
import { eventConfig } from '../config/eventConfig'
import Countdown from '../components/Countdown'
import Divider from '../components/Divider'
import '../styles/home.css'

const dateFormatter = new Intl.DateTimeFormat('en-US', {
  weekday: 'long',
  month: 'long',
  day: 'numeric',
  year: 'numeric',
})

export default function Home() {
  const [open, setOpen] = useState(false)
  const weddingDate = new Date(eventConfig.weddingDateTime)

  return (
    <div className="page">
      <div className="hero">
        <button
          type="button"
          className={`envelope ${open ? 'is-open' : ''}`}
          onClick={() => setOpen(true)}
          aria-expanded={open}
          aria-label="Open the invitation"
        >
          <div className="envelope__body">
            <span className="envelope__initials">{eventConfig.couple.initials}</span>
            {!open && <span className="envelope__hint">Tap to open your invitation</span>}
          </div>
          <div className="envelope__flap" />
        </button>

        {open && (
          <>
            <p className="eyebrow" style={{ marginTop: 24 }}>
              {dateFormatter.format(weddingDate)}
            </p>
            <h1>
              {eventConfig.couple.partner1} &amp; {eventConfig.couple.partner2}
            </h1>
            <p className="hero__tagline">{eventConfig.hero.tagline}</p>
            <Countdown targetDate={eventConfig.weddingDateTime} />
          </>
        )}
      </div>

      <div className={`reveal ${open ? 'is-open' : ''}`}>
        <Divider />

        <section className="section">
          <p className="story-text">{eventConfig.story}</p>
        </section>

        <Divider />

        <section className="section">
          <h2>When &amp; Where</h2>
          <div className="detail-cards">
            <DetailCard {...eventConfig.ceremony} />
            <DetailCard {...eventConfig.reception} />
          </div>
        </section>

        <section className="section">
          <h2>Schedule</h2>
          <ul className="timeline">
            {eventConfig.schedule.map((item) => (
              <li key={item.label}>
                <time>{item.time}</time>
                <span>{item.label}</span>
              </li>
            ))}
          </ul>
        </section>

        <section className="section">
          <h2>Dress Code</h2>
          <p className="story-text">{eventConfig.dressCode}</p>
        </section>

        <div className="rsvp-cta">
          <h2>Will you join us?</h2>
          <p>Kindly respond by {eventConfig.rsvpDeadline}.</p>
          <Link to="/rsvp" style={{ textDecoration: 'none' }}>
            <WaxSealCta />
          </Link>
          <p style={{ marginTop: 24 }}>
            <Link to="/sangeeth">Sangeeth Night invite &rarr;</Link>
          </p>
        </div>

        <footer className="footer">
          <p>
            Questions? Reach us at{' '}
            <a href={`mailto:${eventConfig.contact.email}`}>{eventConfig.contact.email}</a>
          </p>
        </footer>
      </div>
    </div>
  )
}

function DetailCard({ label, venue, address, time, mapUrl }) {
  return (
    <div className="card">
      <p className="eyebrow detail-card__label">{label}</p>
      <p className="detail-card__venue">{venue}</p>
      <p className="detail-card__meta">{address}</p>
      <p className="detail-card__meta">{time}</p>
      <a href={mapUrl} target="_blank" rel="noreferrer">
        View map &rarr;
      </a>
    </div>
  )
}

// Local wrapper so WaxSeal renders inside a Link without nested <button> issues
function WaxSealCta() {
  return (
    <span className="wax-seal wax-seal--lg" role="button">
      <span className="wax-seal__label">RSVP Now</span>
    </span>
  )
}
