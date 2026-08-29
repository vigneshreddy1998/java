// Placeholder for the commissioned hero illustration/video. When that asset exists, swap the
// contents for a full-bleed <video>/<img> and drop the dashed border — the greeting and scroll
// cue can stay as an overlay.
export default function HeroSlot({ coupleNames, guestName }) {
  return (
    <section className="relative min-h-[70vh] m-3 sm:m-4 rounded-2xl border-2 border-dashed border-sand bg-[repeating-linear-gradient(135deg,#fbf9f5,#fbf9f5_12px,#f4efe6_12px,#f4efe6_24px)] flex flex-col items-center justify-center text-center px-6 py-10">
      {guestName && (
        <p className="text-[11px] uppercase tracking-[0.14em] font-bold text-gold-500 mb-3.5">
          Welcome, {guestName}
        </p>
      )}
      <h1 className="font-display font-extrabold text-3xl sm:text-5xl max-w-lg leading-tight mb-3">
        You're invited to celebrate with us
      </h1>
      <p className="font-accent italic text-2xl text-maroon-500 mt-2">{coupleNames}</p>

      <a
        href="#events"
        className="absolute bottom-5 flex flex-col items-center gap-1.5 text-sand text-[10.5px] uppercase tracking-wide"
      >
        <span>Scroll for events</span>
        <svg
          width="16"
          height="16"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="2"
          aria-hidden="true"
          className="motion-safe:animate-bounce"
        >
          <path d="M6 9l6 6 6-6" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
      </a>
    </section>
  )
}
