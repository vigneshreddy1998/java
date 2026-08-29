# Guest-screen designs

Hi-fi designs for the guest side of `rsvp-platform`, authored as Design Component
artboards and published as a design canvas.

**Canvas:** https://claude.ai/code/artifact/31d46418-4ef4-4105-8e54-7f01fafec269

## Files

| File | Screen |
|---|---|
| `Verify.dc.html` | Phone-number front door — identical for every visitor |
| `Main.dc.html` | Home, with a card per event the guest may see |
| `Engagement.dc.html` | Information only, no RSVP |
| `Haldi.dc.html` | Information only, colour theme is the headline |
| `Sangeet.dc.html` | Invite-gated: RSVP plus the song picker |
| `Wedding.dc.html` | RSVP with headcount, companions and meal |
| `System.dc.html` | Palette, type, controls and the arch motif |
| `canvas.json` | Artboard layout, titles and canvas notes |

## Design system

Values are lifted from the running app rather than invented, so designs and code
stay in step — see `../rsvp-platform/frontend/tailwind.config.js` and
`../rsvp-platform/frontend/src/index.css`.

- **Type** — Bricolage Grotesque (headings), Instrument Serif italic (the couple,
  section leads), Inter (everything else)
- **Event accents** — engagement `#8A6C17`, haldi `#B5670A`, sangeet `#0F6B52`,
  wedding `#A11F35`. Accent means *which event you are on*, and nothing else
- **Gold `#C9A227`** is reserved for the commit action at the foot of a form
- **The arch** masks every event card. Its SVG mask needs
  `preserveAspectRatio="none"`, or it letterboxes and silently clips the bottom
  of the card; anything that must escape the shape (the date chip) is a sibling
  of the masked element, not a child

## Editing

Edit the `.dc.html` files here, then re-seed and republish to the same canvas —
the published `wedding-guest-screens.html` is generated output and is gitignored.
