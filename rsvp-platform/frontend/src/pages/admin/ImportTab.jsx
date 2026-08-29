import { useEffect, useState } from 'react'
import { api } from '../../api/client.js'

/**
 * Two-step import: upload produces a preview with suggested names, and nothing is saved
 * until the rows are approved here. A bad name suggestion costs a glance, not a wrong
 * place card.
 */
export default function ImportTab({ token }) {
  const [events, setEvents] = useState([])
  const [preview, setPreview] = useState(null)
  const [rows, setRows] = useState([])
  const [uploading, setUploading] = useState(false)
  const [committing, setCommitting] = useState(false)
  const [result, setResult] = useState(null)
  const [error, setError] = useState(null)

  useEffect(() => {
    api.adminEvents(token).then(setEvents).catch(() => {})
  }, [token])

  async function upload(e) {
    const file = e.target.files?.[0]
    if (!file) return
    setUploading(true)
    setError(null)
    setResult(null)
    try {
      const data = await api.adminPreviewImport(token, file)
      setPreview(data)
      // Everything valid is selected by default; an unparseable number can't be imported.
      setRows(
        data.contacts.map((c) => ({
          ...c,
          selected: !c.invalidPhone,
          name: c.suggestedName,
          eventKeys: [],
        })),
      )
    } catch (err) {
      setError(err.message)
    } finally {
      setUploading(false)
    }
  }

  function updateRow(index, patch) {
    setRows((prev) => prev.map((r, i) => (i === index ? { ...r, ...patch } : r)))
  }

  function toggleEventForAll(eventKey) {
    const allHave = rows.every((r) => !r.selected || r.eventKeys.includes(eventKey))
    setRows((prev) =>
      prev.map((r) =>
        r.selected
          ? {
              ...r,
              eventKeys: allHave
                ? r.eventKeys.filter((k) => k !== eventKey)
                : [...new Set([...r.eventKeys, eventKey])],
            }
          : r,
      ),
    )
  }

  async function commit() {
    const selected = rows.filter((r) => r.selected && !r.invalidPhone)
    if (selected.length === 0) return
    setCommitting(true)
    setError(null)
    try {
      const res = await api.adminCommitImport(
        token,
        selected.map((r) => ({ name: r.name, phone: r.phoneE164 ?? r.phone, eventKeys: r.eventKeys })),
      )
      setResult(res.imported)
      setPreview(null)
      setRows([])
    } catch (err) {
      setError(err.message)
    } finally {
      setCommitting(false)
    }
  }

  const selectedCount = rows.filter((r) => r.selected && !r.invalidPhone).length

  return (
    <div>
      <p className="text-sm text-ink/60 mb-4 max-w-2xl">
        Export your contacts as a <code className="text-xs">.vcf</code> file and upload it here.
        Names are tidied up for review — nothing is saved until you press import.
      </p>

      <input type="file" accept=".vcf,text/vcard" onChange={upload} className="text-sm mb-4" />
      {uploading && <p className="text-sm text-ink/50">Reading contacts...</p>}
      {error && <p className="text-sm text-maroon-500 mb-4">{error}</p>}
      {result !== null && (
        <p className="text-sm text-sangeet-500 font-medium">Imported {result} contact(s).</p>
      )}

      {preview && (
        <>
          <div className="flex flex-wrap items-center gap-4 text-sm text-ink/60 my-4">
            <span>{preview.totalParsed} found</span>
            {preview.invalidCount > 0 && (
              <span className="text-maroon-500">{preview.invalidCount} unusable number(s)</span>
            )}
            {preview.duplicateCount > 0 && (
              <span className="text-haldi-600">{preview.duplicateCount} possible duplicate(s)</span>
            )}
          </div>
          <p className="text-xs text-ink/50 mb-4">{preview.cleanupNote}</p>

          <div className="flex flex-wrap gap-2 mb-4">
            <span className="text-xs uppercase tracking-wide text-ink/50 self-center mr-1">
              Invite all selected to:
            </span>
            {events.map((e) => (
              <button
                key={e.key}
                onClick={() => toggleEventForAll(e.key)}
                className="px-3 py-1.5 rounded-lg border border-gold-300/60 text-xs font-medium hover:bg-parchment"
              >
                {e.name}
              </button>
            ))}
          </div>

          <div className="overflow-x-auto rounded-xl border border-gold-300/50 bg-white">
            <table className="w-full text-sm">
              <thead>
                <tr className="text-left border-b border-gold-300/40">
                  <th className="px-3 py-2"></th>
                  <th className="px-3 py-2 font-medium text-ink/60">As saved</th>
                  <th className="px-3 py-2 font-medium text-ink/60">Import as</th>
                  <th className="px-3 py-2 font-medium text-ink/60">Phone</th>
                  {events.map((e) => (
                    <th key={e.key} className="px-2 py-2 font-medium text-ink/60 text-center">
                      {e.name.replace('The ', '')}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {rows.map((r, i) => (
                  <tr
                    key={i}
                    className={`border-b border-gold-300/20 last:border-0 ${
                      r.invalidPhone ? 'opacity-50' : ''
                    }`}
                  >
                    <td className="px-3 py-2">
                      <input
                        type="checkbox"
                        checked={r.selected}
                        disabled={r.invalidPhone}
                        onChange={(e) => updateRow(i, { selected: e.target.checked })}
                        aria-label={`Include ${r.suggestedName || r.phone}`}
                      />
                    </td>
                    <td className="px-3 py-2 text-ink/50 text-xs">
                      {r.originalName || <span className="italic">no name</span>}
                      {r.duplicateOf.length > 0 && (
                        <div className="text-haldi-600 mt-0.5">also: {r.duplicateOf.join(', ')}</div>
                      )}
                      {r.alreadyExists && <div className="text-ink/40 mt-0.5">already on your list</div>}
                    </td>
                    <td className="px-3 py-2">
                      <input
                        className="w-full min-w-[130px] rounded border border-gold-300/60 px-2 py-1"
                        value={r.name}
                        onChange={(e) => updateRow(i, { name: e.target.value })}
                      />
                    </td>
                    <td className="px-3 py-2 whitespace-nowrap text-ink/70 text-xs">
                      {r.phoneE164 ?? (
                        <span className="text-maroon-500">{r.phone} — can't read</span>
                      )}
                    </td>
                    {events.map((e) => (
                      <td key={e.key} className="px-2 py-2 text-center">
                        <input
                          type="checkbox"
                          disabled={!r.selected || r.invalidPhone}
                          checked={r.eventKeys.includes(e.key)}
                          onChange={(ev) =>
                            updateRow(i, {
                              eventKeys: ev.target.checked
                                ? [...r.eventKeys, e.key]
                                : r.eventKeys.filter((k) => k !== e.key),
                            })
                          }
                          aria-label={`Invite ${r.name || r.phone} to ${e.name}`}
                        />
                      </td>
                    ))}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <button
            onClick={commit}
            disabled={committing || selectedCount === 0}
            className="mt-4 px-5 py-2.5 rounded-lg bg-maroon-500 text-ivory text-sm font-medium disabled:opacity-50"
          >
            {committing ? 'Importing...' : `Import ${selectedCount} contact(s)`}
          </button>
        </>
      )}
    </div>
  )
}
