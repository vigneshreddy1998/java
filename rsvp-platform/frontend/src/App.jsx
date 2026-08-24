import { Route, Routes } from 'react-router-dom'
import NavBar from './components/NavBar.jsx'
import Home from './pages/Home.jsx'
import Wedding from './pages/Wedding.jsx'
import Sangeet from './pages/Sangeet.jsx'
import Travel from './pages/Travel.jsx'
import Gallery from './pages/Gallery.jsx'
import RsvpRedirect from './pages/RsvpRedirect.jsx'
import AdminLogin from './pages/admin/AdminLogin.jsx'
import AdminDashboard from './pages/admin/AdminDashboard.jsx'

export default function App() {
  return (
    <div className="min-h-screen flex flex-col">
      <NavBar />
      <main className="flex-1">
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/rsvp/:inviteToken" element={<RsvpRedirect />} />
          <Route path="/wedding" element={<Wedding />} />
          <Route path="/wedding/:inviteToken" element={<Wedding />} />
          <Route path="/sangeet" element={<Sangeet />} />
          <Route path="/sangeet/:inviteToken" element={<Sangeet />} />
          <Route path="/travel" element={<Travel />} />
          <Route path="/gallery" element={<Gallery />} />
          <Route path="/admin/login" element={<AdminLogin />} />
          <Route path="/admin" element={<AdminDashboard />} />
        </Routes>
      </main>
    </div>
  )
}
