import { Navigate, useParams } from 'react-router-dom'

export default function RsvpRedirect() {
  const { inviteToken } = useParams()
  return <Navigate to={`/wedding/${inviteToken}`} replace />
}
