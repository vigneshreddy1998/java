import { eventConfig } from '../config/eventConfig'

const BASE_URL = eventConfig.apiBaseUrl

async function request(path, options = {}) {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
    ...options,
  })

  const isJson = res.headers.get('content-type')?.includes('application/json')
  const body = isJson ? await res.json().catch(() => null) : await res.text()

  if (!res.ok) {
    const message = (body && body.error) || (typeof body === 'string' ? body : null) ||
      'Something went wrong. Please try again.'
    throw new Error(message)
  }
  return body
}

export function lookupGuestByCode(code) {
  return request(`/api/guests/lookup?code=${encodeURIComponent(code)}`)
}

export function submitRsvp(payload) {
  return request('/api/rsvp', { method: 'POST', body: JSON.stringify(payload) })
}

export function checkExistingRsvp(email) {
  return request(`/api/rsvp/check?email=${encodeURIComponent(email)}`)
}

export function fetchAdminSummary(adminKey) {
  return request('/api/admin/rsvps', { headers: { 'X-Admin-Key': adminKey } })
}

export function adminExportUrl() {
  return `${BASE_URL}/api/admin/export`
}
