export default function Gallery() {
  const placeholders = Array.from({ length: 6 })

  return (
    <div className="max-w-4xl mx-auto px-6 py-16">
      <h1 className="text-4xl font-display text-maroon-500 text-center mb-10">Gallery</h1>
      <div className="grid grid-cols-2 sm:grid-cols-3 gap-4">
        {placeholders.map((_, i) => (
          <div key={i} className="aspect-square rounded-lg bg-parchment flex items-center justify-center text-ink/30 text-xs">
            Photo {i + 1}
          </div>
        ))}
      </div>
    </div>
  )
}
