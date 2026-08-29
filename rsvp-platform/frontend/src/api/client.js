const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8081'

/**
 * The guest session token lives here — a module-level variable, never localStorage and never
 * the URL. That is what makes "ask every time" real rather than cosmetic: a refresh drops the
 * token and the guest verifies again.
 */
let guestToken = null

export function setGuestToken(token) {
  guestToken = token
}

export function clearGuestToken() {
  guestToken = null
}

export function hasGuestToken() {
  return guestToken !== null
}

/** Thrown so callers can distinguish an expired session from a genuine error. */
export class ApiError extends Error {
  constructor(message, status) {
    super(message)
    this.status = status
  }
}

async function request(path, { method = 'GET', body, token } = {}) {
  const headers = { 'Content-Type': 'application/json' }
  const auth = token !== undefined ? token : guestToken
  if (auth) headers.Authorization = `Bearer ${auth}`

  const res = await fetch(`${API_URL}${path}`, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  })

  if (!res.ok) {
    let message = `Request failed (${res.status})`
    try {
      const data = await res.json()
      if (data.message) message = data.message
    } catch {
      // non-JSON error body
    }
    throw new ApiError(message, res.status)
  }

  return res.status === 204 ? null : res.json()
}

export const api = {
  verify: (phone, name) => request('/api/verify', { method: 'POST', body: { phone, name }, token: null }),
  me: () => request('/api/me'),
  getEvent: (key) => request(`/api/events/${key}`),
  getSongs: (key) => request(`/api/events/${key}/songs`),
  submitRsvp: (key, payload) => request(`/api/events/${key}/rsvp`, { method: 'POST', body: payload }),

  adminLogin: (username, password) =>
    request('/api/admin/auth/login', { method: 'POST', body: { username, password }, token: null }),
  adminOverview: (t) => request('/api/admin/overview', { token: t }),
  adminGuests: (t) => request('/api/admin/guests', { token: t }),
  adminUpdateInvites: (t, id, eventKeys) =>
    request(`/api/admin/guests/${id}/invites`, { method: 'PUT', body: { eventKeys }, token: t }),
  adminPromote: (t, id) => request(`/api/admin/guests/${id}/promote`, { method: 'POST', token: t }),
  adminSongs: (t) => request('/api/admin/songs', { token: t }),
  adminAddSong: (t, payload) => request('/api/admin/songs', { method: 'POST', body: payload, token: t }),
  adminDeleteSong: (t, id) => request(`/api/admin/songs/${id}`, { method: 'DELETE', token: t }),
  adminEvents: (t) => request('/api/admin/events', { token: t }),
  adminUpdateEvent: (t, key, payload) =>
    request(`/api/admin/events/${key}`, { method: 'PUT', body: payload, token: t }),
  adminCommitImport: (t, rows) =>
    request('/api/admin/contacts/commit', { method: 'POST', body: { rows }, token: t }),

  adminPreviewImport: async (t, file) => {
    const form = new FormData()
    form.append('file', file)
    const res = await fetch(`${API_URL}/api/admin/contacts/preview`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${t}` },
      body: form,
    })
    if (!res.ok) throw new ApiError(`Could not read that file (${res.status})`, res.status)
    return res.json()
  },
}
