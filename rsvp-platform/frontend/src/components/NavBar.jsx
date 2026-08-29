import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useGuestSession } from '../session/GuestSession.jsx'
import { siteConfig } from '../config/siteConfig.js'

/**
 * The nav is built from the session's event list, so an event the guest can't see never
 * appears here. That's presentation only — the server enforces the same rule on every
 * request, since hiding a link is not access control.
 */
export default function NavBar() {
  const { isVerified, events, signOut } = useGuestSession()
  const [menuOpen, setMenuOpen] = useState(false)
  const navigate = useNavigate()

  const links = isVerified
    ? [['Home', '/home'], ...events.map((e) => [e.name, `/event/${e.key}`]), ['Gallery', '/gallery']]
    : []

  function handleSignOut() {
    setMenuOpen(false)
    signOut()
    navigate('/')
  }

  return (
    <header className="sticky top-0 z-30 bg-ivory/90 backdrop-blur border-b border-gold-300/40">
      <nav className="max-w-5xl mx-auto flex items-center justify-between px-6 py-4">
        <Link
          to={isVerified ? '/home' : '/'}
          className="font-accent italic text-xl text-maroon-500"
          onClick={() => setMenuOpen(false)}
        >
          {siteConfig.coupleNames}
        </Link>

        {isVerified && (
          <>
            <ul className="hidden md:flex items-center gap-5 text-sm uppercase tracking-wide text-ink/70">
              {links.map(([label, href]) => (
                <li key={href}>
                  <Link to={href} className="hover:text-maroon-500 transition-colors">
                    {label}
                  </Link>
                </li>
              ))}
              <li>
                <button onClick={handleSignOut} className="text-ink/40 hover:text-ink transition-colors">
                  Not you?
                </button>
              </li>
            </ul>

            <button
              type="button"
              className="md:hidden p-2 -mr-2 text-ink"
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
          </>
        )}
      </nav>

      {menuOpen && isVerified && (
        <ul id="mobile-nav-menu" className="md:hidden border-t border-gold-300/40 px-6 py-3 space-y-1">
          {links.map(([label, href]) => (
            <li key={href}>
              <Link
                to={href}
                onClick={() => setMenuOpen(false)}
                className="block py-2 text-sm uppercase tracking-wide text-ink/70 hover:text-maroon-500 transition-colors"
              >
                {label}
              </Link>
            </li>
          ))}
          <li>
            <button
              onClick={handleSignOut}
              className="block py-2 text-sm uppercase tracking-wide text-ink/40"
            >
              Not you?
            </button>
          </li>
        </ul>
      )}
    </header>
  )
}
