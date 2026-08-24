const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8081'

async function request(path, { method = 'GET', body, token } = {}) {
  const headers = { 'Content-Type': 'application/json' }
  if (token) headers.Authorization = `Bearer ${token}`

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
      // ignore non-JSON error bodies
    }
    throw new Error(message)
  }

  if (res.status === 204) return null
  return res.json()
}

export const api = {
  getEvents: () => request('/api/events'),
  getFamily: (inviteToken) => request(`/api/families/${inviteToken}`),
  submitRsvp: (payload) => request('/api/rsvp', { method: 'POST', body: payload }),

  listSongs: () => request('/api/songs'),
  claimSong: (songId, familyInviteToken) =>
    request(`/api/songs/${songId}/claim`, { method: 'POST', body: { familyInviteToken } }),

  chatFlightDetails: (guestId, messages) =>
    request('/api/chat/flight-details', { method: 'POST', body: { guestId, messages } }),
  submitFlightDetailsManually: (payload) =>
    request('/api/flight-details', { method: 'POST', body: payload }),

  adminLogin: (username, password) =>
    request('/api/admin/auth/login', { method: 'POST', body: { username, password } }),
  adminTracker: (token, params = {}) => {
    const qs = new URLSearchParams(params).toString()
    return request(`/api/admin/rsvps${qs ? `?${qs}` : ''}`, { token })
  },
  adminMealSummary: (token, eventType) =>
    request(`/api/admin/meals-summary?eventType=${eventType}`, { token }),
  adminLogistics: (token) => request('/api/admin/logistics', { token }),
  adminNonResponders: (token) => request('/api/admin/non-responders', { token }),
  adminSendReminders: (token) =>
    request('/api/admin/reminders/send', { method: 'POST', token }),
  adminCreateSong: (token, payload) =>
    request('/api/admin/songs', { method: 'POST', body: payload, token }),
  adminUpdateEvent: (token, type, payload) =>
    request(`/api/admin/events/${type}`, { method: 'PUT', body: payload, token }),
  adminImportGuests: async (token, file) => {
    const form = new FormData()
    form.append('file', file)
    const res = await fetch(`${API_URL}/api/admin/guests/import`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}` },
      body: form,
    })
    if (!res.ok) throw new Error(`Import failed (${res.status})`)
    return res.json()
  },
}
