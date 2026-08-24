import { Link, useParams } from 'react-router-dom'

export default function NavBar() {
  const { inviteToken } = useParams()
  const suffix = inviteToken ? `/${inviteToken}` : ''

  const links = [
    ['Home', '/'],
    ['Wedding', `/wedding${suffix}`],
    ['Sangeet', `/sangeet${suffix}`],
    ['Travel & Stay', '/travel'],
    ['Gallery', '/gallery'],
  ]

  return (
    <header className="sticky top-0 z-30 bg-ivory/90 backdrop-blur border-b border-gold-300/40">
      <nav className="max-w-5xl mx-auto flex items-center justify-between px-6 py-4">
        <Link to="/" className="font-display text-lg tracking-wide text-maroon-500">
          Our Wedding
        </Link>
        <ul className="hidden sm:flex gap-6 text-sm uppercase tracking-wide text-ink/70">
          {links.map(([label, href]) => (
            <li key={label}>
              <Link to={href} className="hover:text-maroon-500 transition-colors">
                {label}
              </Link>
            </li>
          ))}
        </ul>
      </nav>
    </header>
  )
}
