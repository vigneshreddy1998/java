import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../../api/client.js'

export default function AdminLogin() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  async function submit(e) {
    e.preventDefault()
    setLoading(true)
    setError(null)
    try {
      const { token } = await api.adminLogin(username, password)
      localStorage.setItem('admin_token', token)
      navigate('/admin')
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="max-w-sm mx-auto px-6 py-24">
      <h1 className="text-2xl font-display text-center mb-8">Admin login</h1>
      <form onSubmit={submit} className="space-y-4">
        <input
          className="w-full rounded-lg border border-gold-300/60 px-3 py-2 text-sm"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <input
          type="password"
          className="w-full rounded-lg border border-gold-300/60 px-3 py-2 text-sm"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        {error && <p className="text-sm text-maroon-500">{error}</p>}
        <button
          disabled={loading}
          className="w-full py-2 rounded-lg bg-maroon-500 text-ivory font-medium text-sm disabled:opacity-50"
        >
          {loading ? 'Signing in...' : 'Sign in'}
        </button>
      </form>
    </div>
  )
}
