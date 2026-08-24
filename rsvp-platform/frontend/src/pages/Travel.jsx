export default function Travel() {
  return (
    <div className="max-w-3xl mx-auto px-6 py-16">
      <h1 className="text-4xl font-display text-maroon-500 text-center mb-10">Travel &amp; Stay</h1>

      <div className="grid sm:grid-cols-2 gap-6">
        <div className="rounded-xl border border-gold-300/50 bg-white p-6">
          <h2 className="font-display text-xl mb-2">Getting there</h2>
          <p className="text-sm text-ink/70">
            The nearest airport details will be posted here. If you're flying in, RSVP first — we'll
            follow up to grab your flight details and arrange pickup.
          </p>
        </div>
        <div className="rounded-xl border border-gold-300/50 bg-white p-6">
          <h2 className="font-display text-xl mb-2">Where to stay</h2>
          <p className="text-sm text-ink/70">
            Recommended hotels and our room block details will go here once confirmed.
          </p>
        </div>
      </div>

      <div className="mt-8 rounded-xl border border-gold-300/50 bg-white p-6">
        <h2 className="font-display text-xl mb-2">Map</h2>
        <div className="aspect-video rounded-lg bg-parchment flex items-center justify-center text-ink/40 text-sm">
          Venue map coming soon
        </div>
      </div>
    </div>
  )
}
