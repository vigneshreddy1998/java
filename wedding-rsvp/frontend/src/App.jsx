import { Routes, Route } from 'react-router-dom'
import Home from './pages/Home'
import Rsvp from './pages/Rsvp'
import ThankYou from './pages/ThankYou'
import Admin from './pages/Admin'
import Sangeeth from './pages/Sangeeth'

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/rsvp" element={<Rsvp />} />
      <Route path="/thank-you" element={<ThankYou />} />
      <Route path="/admin" element={<Admin />} />
      <Route path="/sangeeth" element={<Sangeeth />} />
    </Routes>
  )
}
