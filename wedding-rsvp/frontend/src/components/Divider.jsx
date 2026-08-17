export default function Divider() {
  return (
    <div className="divider" role="presentation">
      <svg width="120" height="16" viewBox="0 0 120 16" fill="none">
        <line x1="0" y1="8" x2="42" y2="8" stroke="currentColor" strokeWidth="1" />
        <path
          d="M60 8c-4-6-10-6-12 0 2 6 8 6 12 0zm0 0c4-6 10-6 12 0-2 6-8 6-12 0z"
          stroke="currentColor"
          strokeWidth="1"
          fill="none"
        />
        <line x1="78" y1="8" x2="120" y2="8" stroke="currentColor" strokeWidth="1" />
      </svg>
    </div>
  )
}
