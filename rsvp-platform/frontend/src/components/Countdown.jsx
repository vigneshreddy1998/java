import { useEffect, useState } from 'react'

function getTimeLeft(targetDate) {
  const diff = Math.max(0, new Date(targetDate).getTime() - Date.now())
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  const hours = Math.floor((diff / (1000 * 60 * 60)) % 24)
  const minutes = Math.floor((diff / (1000 * 60)) % 60)
  const seconds = Math.floor((diff / 1000) % 60)
  return { days, hours, minutes, seconds }
}

export default function Countdown({ targetDate }) {
  const [timeLeft, setTimeLeft] = useState(() => getTimeLeft(targetDate))

  useEffect(() => {
    const id = setInterval(() => setTimeLeft(getTimeLeft(targetDate)), 1000)
    return () => clearInterval(id)
  }, [targetDate])

  const units = [
    ['Days', timeLeft.days],
    ['Hours', timeLeft.hours],
    ['Minutes', timeLeft.minutes],
    ['Seconds', timeLeft.seconds],
  ]

  return (
    <div className="flex gap-4 sm:gap-8 justify-center">
      {units.map(([label, value]) => (
        <div key={label} className="text-center">
          <div className="text-3xl sm:text-5xl font-display font-medium text-maroon-500 tabular-nums">
            {String(value).padStart(2, '0')}
          </div>
          <div className="text-xs sm:text-sm uppercase tracking-widest text-ink/60 mt-1">{label}</div>
        </div>
      ))}
    </div>
  )
}
