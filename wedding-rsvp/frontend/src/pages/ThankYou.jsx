import { Link, useLocation } from 'react-router-dom'
import { eventConfig } from '../config/eventConfig'

export default function ThankYou() {
  const location = useLocation()
  const attending = location.state?.attending ?? true

  return (
    <div className="page" style={{ textAlign: 'center', paddingTop: '80px' }}>
      <p className="eyebrow">RSVP Received</p>
      <h1 style={{ marginTop: 12 }}>
        {attending ? "We can't wait to celebrate with you!" : "We'll miss you, but thank you for letting us know"}
      </h1>
      <p style={{ color: 'var(--charcoal-soft)', marginTop: 16, maxWidth: 440, marginInline: 'auto' }}>
        {attending
          ? `Mark your calendar for ${new Date(eventConfig.weddingDateTime).toLocaleDateString('en-US', {
              month: 'long',
              day: 'numeric',
              year: 'numeric',
            })}. Reach out anytime at ${eventConfig.contact.email} with questions.`
          : `You'll be in our thoughts. Feel free to reach out at ${eventConfig.contact.email} anytime.`}
      </p>
      <Link to="/" style={{ display: 'inline-block', marginTop: 32, color: 'var(--wine)' }}>
        &larr; Back to the invitation
      </Link>
    </div>
  )
}
