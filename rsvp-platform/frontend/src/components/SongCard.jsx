export default function SongCard({ song, onClaim, claiming }) {
  return (
    <div className="rounded-xl border border-gold-300/50 bg-white p-4 flex flex-col gap-3">
      <div className="flex items-start justify-between gap-2">
        <h3 className="font-display text-lg text-ink">{song.title}</h3>
        {song.locked ? (
          <span className="text-xs uppercase tracking-wide bg-parchment text-maroon-500 px-2 py-1 rounded-full whitespace-nowrap">
            Taken
          </span>
        ) : (
          <span className="text-xs uppercase tracking-wide bg-gold-300/30 text-gold-500 px-2 py-1 rounded-full whitespace-nowrap">
            Available
          </span>
        )}
      </div>

      {song.practiceVideoUrl && (
        <div className="aspect-video rounded-lg overflow-hidden bg-black/5">
          <iframe
            src={song.practiceVideoUrl}
            title={song.title}
            className="w-full h-full"
            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
            allowFullScreen
          />
        </div>
      )}

      {song.locked ? (
        <p className="text-sm text-ink/60">Claimed by {song.claimedByFamilyName || 'another family'}</p>
      ) : (
        <button
          onClick={() => onClaim(song.id)}
          disabled={claiming}
          className="mt-auto py-2 rounded-lg bg-maroon-500 text-ivory text-sm font-medium disabled:opacity-50"
        >
          {claiming ? 'Claiming...' : 'Claim this song'}
        </button>
      )}
    </div>
  )
}
