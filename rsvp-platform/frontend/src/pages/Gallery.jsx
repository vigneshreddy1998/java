import { siteConfig } from '../config/siteConfig.js'

export default function Gallery() {
  return (
    <div className="max-w-2xl mx-auto px-6 py-16 text-center">
      <h1 className="text-4xl font-display text-maroon-500 mb-4">Gallery</h1>

      {siteConfig.galleryUrl ? (
        <>
          <p className="text-ink/60 mb-8">Photos from the celebrations, all in one album.</p>
          <a
            href={siteConfig.galleryUrl}
            target="_blank"
            rel="noreferrer"
            className="inline-block px-6 py-3 rounded-lg bg-maroon-500 text-ivory font-medium"
          >
            Open the album
          </a>
        </>
      ) : (
        <p className="text-ink/60">
          The album isn't up yet — we'll share it here once there are photos worth showing.
        </p>
      )}
    </div>
  )
}
