import { useEffect, useRef, useState } from 'react'

/**
 * Reveals an element as it scrolls into view. Returns a ref to attach and a
 * boolean for the current visibility, so callers can drive either the CSS
 * `.reveal`/`.in-view` classes or their own transition.
 *
 * Respects prefers-reduced-motion (starts already visible) and falls back to
 * "always visible" in environments without IntersectionObserver (SSR, very
 * old browsers) rather than leaving content permanently hidden.
 */
export function useScrollReveal({ threshold = 0.2 } = {}) {
  const ref = useRef(null)
  const prefersReducedMotion =
    typeof window !== 'undefined' && window.matchMedia?.('(prefers-reduced-motion: reduce)').matches
  const [inView, setInView] = useState(
    prefersReducedMotion || typeof IntersectionObserver === 'undefined',
  )

  useEffect(() => {
    if (inView || typeof IntersectionObserver === 'undefined') return
    const el = ref.current
    if (!el) return

    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            setInView(true)
            observer.unobserve(entry.target)
          }
        })
      },
      { threshold },
    )
    observer.observe(el)
    return () => observer.disconnect()
  }, [inView, threshold])

  return [ref, inView]
}
