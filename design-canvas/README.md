# Guest-screen designs

Hi-fi designs for the guest side of `rsvp-platform`, authored as Design Component
artboards and published as a design canvas.

**Canvas:** https://claude.ai/code/artifact/31d46418-4ef4-4105-8e54-7f01fafec269

## Files

The canvas has two pages: **Desktop** and **Mobile &amp; states**.

### Desktop

| File | Screen |
|---|---|
| `Verify.dc.html` | Phone-number front door — identical for every visitor |
| `Main.dc.html` | Home, with a card per event the guest may see |
| `Engagement.dc.html` | Information only, no RSVP |
| `Haldi.dc.html` | Information only, colour theme is the headline |
| `Sangeet.dc.html` | Invite-gated: RSVP plus the song picker |
| `Wedding.dc.html` | RSVP with headcount, companions and meal |
| `System.dc.html` | Palette, type, controls and the arch motif |

### Mobile &amp; states

Guests arrive from a WhatsApp link, so most will only ever see the phone screens.

| File | Screen |
|---|---|
| `MobileHome.dc.html` | Home at 390px — stacked event cards |
| `MobileWedding.dc.html` | Wedding RSVP at 390px |
| `MobileSangeet.dc.html` | Sangeet + song picker at 390px — the tightest layout on the site |
| `States.dc.html` | Number not recognised, rate-limited, declined, empty song list, mobile menu |

`canvas.json` holds the artboard layout, page assignment, titles and canvas notes.

Mobile rules worth keeping: every tap target clears 44px, paired buttons stack rather
than sit side by side, and song rows give the title, the running time and the overlap
flag their own lines instead of competing for 390px.

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
