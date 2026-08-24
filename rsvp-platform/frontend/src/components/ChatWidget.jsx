import { useEffect, useRef, useState } from 'react'
import { api } from '../api/client'

const GREETING = "Let's get your flight details so we can plan pickup! When are you flying in, and what airport?"

export default function ChatWidget({ guestId, onClose }) {
  const [messages, setMessages] = useState([{ role: 'assistant', content: GREETING }])
  const [input, setInput] = useState('')
  const [sending, setSending] = useState(false)
  const [complete, setComplete] = useState(false)
  const [showManualForm, setShowManualForm] = useState(false)
  const [error, setError] = useState(null)
  const scrollRef = useRef(null)

  useEffect(() => {
    scrollRef.current?.scrollTo({ top: scrollRef.current.scrollHeight, behavior: 'smooth' })
  }, [messages])

  async function send() {
    if (!input.trim() || sending) return
    const nextMessages = [...messages, { role: 'user', content: input.trim() }]
    setMessages(nextMessages)
    setInput('')
    setSending(true)
    setError(null)
    try {
      const res = await api.chatFlightDetails(guestId, nextMessages)
      setMessages([...nextMessages, { role: 'assistant', content: res.reply }])
      if (res.complete) setComplete(true)
    } catch (e) {
      setError("Couldn't reach the assistant — you can use the manual form below instead.")
      setShowManualForm(true)
    } finally {
      setSending(false)
    }
  }

  return (
    <div className="fixed bottom-4 right-4 z-50 w-full max-w-sm bg-white rounded-2xl shadow-soft border border-gold-300/40 flex flex-col overflow-hidden">
      <div className="flex items-center justify-between px-4 py-3 bg-maroon-500 text-ivory">
        <span className="font-display">Flight details</span>
        <button onClick={onClose} className="text-ivory/80 hover:text-ivory" aria-label="Close chat">
          &times;
        </button>
      </div>

      <div ref={scrollRef} className="flex-1 max-h-80 overflow-y-auto px-4 py-3 space-y-3 text-sm">
        {messages.map((m, i) => (
          <div
            key={i}
            className={`max-w-[85%] px-3 py-2 rounded-xl ${
              m.role === 'user'
                ? 'bg-maroon-50 ml-auto text-ink'
                : 'bg-parchment text-ink'
            }`}
          >
            {m.content}
          </div>
        ))}
        {complete && (
          <div className="text-xs text-maroon-500 font-medium">
            Saved! We'll see you at pickup. You can close this window.
          </div>
        )}
        {error && <div className="text-xs text-maroon-500">{error}</div>}
      </div>

      {!complete && (
        <div className="border-t border-gold-300/40 p-3 flex gap-2">
          <input
            className="flex-1 rounded-lg border border-gold-300/60 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-maroon-400"
            placeholder="Type your reply..."
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && send()}
            disabled={sending}
          />
          <button
            onClick={send}
            disabled={sending}
            className="px-3 py-2 rounded-lg bg-maroon-500 text-ivory text-sm font-medium disabled:opacity-50"
          >
            Send
          </button>
        </div>
      )}

      <div className="px-4 pb-3">
        <button
          onClick={() => setShowManualForm((s) => !s)}
          className="text-xs text-ink/60 underline"
        >
          {showManualForm ? 'Hide manual form' : "Prefer a form instead?"}
        </button>
        {showManualForm && <ManualFlightForm guestId={guestId} onSaved={() => setComplete(true)} />}
      </div>
    </div>
  )
}

function ManualFlightForm({ guestId, onSaved }) {
  const [form, setForm] = useState({
    flightNumber: '',
    arrivalDatetime: '',
    airport: '',
    pickupNeeded: true,
  })
  const [saving, setSaving] = useState(false)

  async function submit(e) {
    e.preventDefault()
    setSaving(true)
    try {
      await api.submitFlightDetailsManually({
        guestId,
        flightNumber: form.flightNumber || null,
        arrivalDatetime: form.arrivalDatetime || null,
        airport: form.airport || null,
        pickupNeeded: form.pickupNeeded,
      })
      onSaved()
    } finally {
      setSaving(false)
    }
  }

  return (
    <form onSubmit={submit} className="mt-2 space-y-2 text-sm">
      <input
        className="w-full rounded-lg border border-gold-300/60 px-3 py-2"
        placeholder="Flight number"
        value={form.flightNumber}
        onChange={(e) => setForm({ ...form, flightNumber: e.target.value })}
      />
      <input
        type="datetime-local"
        className="w-full rounded-lg border border-gold-300/60 px-3 py-2"
        value={form.arrivalDatetime}
        onChange={(e) => setForm({ ...form, arrivalDatetime: e.target.value })}
      />
      <input
        className="w-full rounded-lg border border-gold-300/60 px-3 py-2"
        placeholder="Arrival airport"
        value={form.airport}
        onChange={(e) => setForm({ ...form, airport: e.target.value })}
      />
      <label className="flex items-center gap-2">
        <input
          type="checkbox"
          checked={form.pickupNeeded}
          onChange={(e) => setForm({ ...form, pickupNeeded: e.target.checked })}
        />
        I need airport pickup
      </label>
      <button
        type="submit"
        disabled={saving}
        className="w-full py-2 rounded-lg bg-maroon-500 text-ivory font-medium disabled:opacity-50"
      >
        {saving ? 'Saving...' : 'Save flight details'}
      </button>
    </form>
  )
}
