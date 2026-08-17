// ─────────────────────────────────────────────────────────────────────────
// Edit this file with your real wedding details. Nothing else in the
// codebase should need to change for basic customization.
// ─────────────────────────────────────────────────────────────────────────

import sangeethPhoto from '../assets/sangeeth-photo.jpg'

export const eventConfig = {
  couple: {
    partner1: 'Akshath Reddy',
    partner2: 'Sonalika',
    initials: 'A & S', // shown on the envelope seal
  },

  // ISO datetime used for the countdown + calendar links
  weddingDateTime: '2027-02-14T17:00:00',
  rsvpDeadline: 'January 10, 2027',

  hero: {
    tagline: 'Together with our families, we joyfully invite you to celebrate our wedding',
  },

  story:
    "Two paragraphs about how you met, got engaged, or whatever you'd like guests to " +
    'read here. Keep it short and warm — this replaces the placeholder text in ' +
    'src/config/eventConfig.js.',

  ceremony: {
    label: 'Ceremony',
    venue: 'Venue Name',
    address: '123 Example Street, Charlotte, NC',
    time: '4:00 PM',
    mapUrl: 'https://maps.google.com/?q=Venue+Name+Charlotte+NC',
  },

  reception: {
    label: 'Reception',
    venue: 'Venue Name',
    address: '123 Example Street, Charlotte, NC',
    time: '6:30 PM',
    mapUrl: 'https://maps.google.com/?q=Venue+Name+Charlotte+NC',
  },

  schedule: [
    { time: '4:00 PM', label: 'Ceremony' },
    { time: '5:00 PM', label: 'Cocktail Hour' },
    { time: '6:30 PM', label: 'Dinner & Reception' },
    { time: '10:00 PM', label: 'Send-off' },
  ],

  dressCode: 'Formal attire',

  // If true, guests must enter an invite code before RSVPing (prevents
  // uninvited submissions). If false, anyone with the link can RSVP with
  // their name + email. Toggle this based on whether you've pre-loaded a
  // guest list via the admin panel.
  requireInviteCode: false,

  mealOptions: ['Chicken', 'Vegetarian', 'Vegan', "Kids' meal"],

  contact: {
    name: 'Your Name',
    email: 'you@example.com',
    phone: '',
  },

  // Set in .env.local / your hosting provider as VITE_API_URL. Falls back
  // to localhost for local development.
  apiBaseUrl: import.meta.env.VITE_API_URL || 'http://localhost:8080',
}

// ─────────────────────────────────────────────────────────────────────────
// The Sangeeth Night invite (src/pages/Sangeeth.jsx) is a separate,
// self-contained "music player" experience at /sangeeth — edit its details
// here. RSVPs from this page are stored with eventType "SANGEETH" so they
// show up distinctly in /admin, alongside the main wedding RSVPs.
// ─────────────────────────────────────────────────────────────────────────
export const sangeethConfig = {
  eventType: 'SANGEETH',
  coupleNames: [eventConfig.couple.partner1, eventConfig.couple.partner2],
  eventDate: 'Friday, November 13, 2026',
  eventTime: '6:00 PM – 11:00 PM',
  photoUrl: sangeethPhoto,
  maxParty: 8,
  mealOptions: ['Vegetarian', 'Non-Vegetarian'],
}
