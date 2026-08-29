import { createContext, useCallback, useContext, useMemo, useState } from 'react'
import { api, clearGuestToken, setGuestToken } from '../api/client.js'

const GuestSessionContext = createContext(null)

/**
 * Holds the verified guest for the life of the tab. Nothing is persisted — closing or
 * refreshing the page drops the session and the guest verifies again, which is the behaviour
 * that was chosen deliberately over remembering the device.
 */
export function GuestSessionProvider({ children }) {
  const [me, setMe] = useState(null)
  const [loading, setLoading] = useState(false)

  const verify = useCallback(async (phone, name) => {
    setLoading(true)
    try {
      const { token } = await api.verify(phone, name)
      setGuestToken(token)
      const profile = await api.me()
      setMe(profile)
      return profile
    } finally {
      setLoading(false)
    }
  }, [])

  const refresh = useCallback(async () => {
    const profile = await api.me()
    setMe(profile)
    return profile
  }, [])

  const signOut = useCallback(() => {
    clearGuestToken()
    setMe(null)
  }, [])

  const value = useMemo(
    () => ({
      me,
      loading,
      verify,
      refresh,
      signOut,
      isVerified: me !== null,
      /** Authoritative on the client too: the server sends only events this guest may see. */
      events: me?.events ?? [],
      rsvpFor: (key) => me?.rsvps?.find((r) => r.eventKey === key) ?? null,
    }),
    [me, loading, verify, refresh, signOut],
  )

  return <GuestSessionContext.Provider value={value}>{children}</GuestSessionContext.Provider>
}

export function useGuestSession() {
  const ctx = useContext(GuestSessionContext)
  if (!ctx) throw new Error('useGuestSession must be used inside GuestSessionProvider')
  return ctx
}
