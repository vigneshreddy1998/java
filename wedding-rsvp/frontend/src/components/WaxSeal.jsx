export default function WaxSeal({ children, onClick, type = 'button', size = 'md', disabled = false }) {
  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled}
      className={`wax-seal wax-seal--${size}`}
    >
      <span className="wax-seal__ring" aria-hidden="true" />
      <span className="wax-seal__label">{children}</span>
    </button>
  )
}
