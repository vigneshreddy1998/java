import { Navigate, Route, Routes } from 'react-router-dom'
import NavBar from './components/NavBar.jsx'
import Verify from './pages/Verify.jsx'
import Home from './pages/Home.jsx'
import EventPage from './pages/EventPage.jsx'
import Gallery from './pages/Gallery.jsx'
import AdminLogin from './pages/admin/AdminLogin.jsx'
import AdminDashboard from './pages/admin/AdminDashboard.jsx'
import { GuestSessionProvider, useGuestSession } from './session/GuestSession.jsx'

/**
 * Guest pages need a verified session. Because the session lives in memory only, a refresh
 * lands here and sends the guest back to verify — which is the intended behaviour, not a bug.
 */
function RequireGuest({ children }) {
  const { isVerified } = useGuestSession()
  return isVerified ? children : <Navigate to="/" replace />
}

function GuestRoutes() {
  return (
    <div className="min-h-screen flex flex-col">
      <NavBar />
      <main className="flex-1">
        <Routes>
          <Route path="/" element={<Verify />} />
          <Route path="/home" element={<RequireGuest><Home /></RequireGuest>} />
          <Route path="/event/:eventKey" element={<RequireGuest><EventPage /></RequireGuest>} />
          <Route path="/gallery" element={<RequireGuest><Gallery /></RequireGuest>} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
    </div>
  )
}

export default function App() {
  return (
    <Routes>
      {/* Admin lives outside the guest session entirely — different credential, different shell. */}
      <Route path="/admin/login" element={<AdminLogin />} />
      <Route path="/admin" element={<AdminDashboard />} />
      <Route
        path="*"
        element={
          <GuestSessionProvider>
            <GuestRoutes />
          </GuestSessionProvider>
        }
      />
    </Routes>
  )
}
