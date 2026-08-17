import { useEffect, useState } from 'react'

function getDaysRemaining(targetIso) {
  const target = new Date(targetIso).getTime()
  const now = Date.now()
  const diff = target - now
  return Math.max(0, Math.ceil(diff / (1000 * 60 * 60 * 24)))
}

export default function Countdown({ targetDate }) {
  const [days, setDays] = useState(() => getDaysRemaining(targetDate))

  useEffect(() => {
    const id = setInterval(() => setDays(getDaysRemaining(targetDate)), 1000 * 60 * 60)
    return () => clearInterval(id)
  }, [targetDate])

  if (days <= 0) {
    return <p className="countdown">We're celebrating today &mdash; see you there!</p>
  }

  return (
    <p className="countdown">
      <span className="countdown__number">{days}</span>
      <span className="countdown__label">{days === 1 ? 'day to go' : 'days to go'}</span>
    </p>
  )
}
