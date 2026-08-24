import { useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { siteConfig } from '../config/siteConfig.js'

export default function NavBar() {
  const { inviteToken } = useParams()
  const [menuOpen, setMenuOpen] = useState(false)
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
        <Link
          to="/"
          className="font-accent italic text-xl text-maroon-500"
          onClick={() => setMenuOpen(false)}
        >
          {siteConfig.coupleNames}
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

        <button
          type="button"
          className="sm:hidden p-2 -mr-2 text-ink"
          aria-label={menuOpen ? 'Close menu' : 'Open menu'}
          aria-expanded={menuOpen}
          aria-controls="mobile-nav-menu"
          onClick={() => setMenuOpen((open) => !open)}
        >
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
            {menuOpen ? (
              <path d="M6 6l12 12M18 6L6 18" strokeLinecap="round" />
            ) : (
              <path d="M4 7h16M4 12h16M4 17h16" strokeLinecap="round" />
            )}
          </svg>
        </button>
      </nav>

      {menuOpen && (
        <ul id="mobile-nav-menu" className="sm:hidden border-t border-gold-300/40 px-6 py-3 space-y-1">
          {links.map(([label, href]) => (
            <li key={label}>
              <Link
                to={href}
                onClick={() => setMenuOpen(false)}
                className="block py-2 text-sm uppercase tracking-wide text-ink/70 hover:text-maroon-500 transition-colors"
              >
                {label}
              </Link>
            </li>
          ))}
        </ul>
      )}
    </header>
  )
}
