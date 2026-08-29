/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      fontFamily: {
        // Display headings (event names, big numerals)
        display: ['"Bricolage Grotesque"', 'sans-serif'],
        // Italic accent serif (couple name, pull-quotes) — used sparingly
        accent: ['"Instrument Serif"', 'serif'],
        body: ['"Inter"', 'sans-serif'],
      },
      colors: {
        ivory: '#FFFFFF',
        parchment: '#F4EFE6',
        sand: '#DCD3C4',
        // Wedding accent — also the "wed" panel color from the brand mockup
        maroon: {
          50: '#FBEEF0',
          100: '#F3D3D8',
          400: '#C23A50',
          500: '#A11F35',
          600: '#7D1729',
          700: '#5C0F1E',
        },
        // Sangeet accent
        sangeet: {
          50: '#EAF5F0',
          400: '#1D8A6C',
          500: '#0F6B52',
          600: '#0B4E3C',
        },
        // Haldi — turmeric
        haldi: {
          50: '#FCF3E3',
          400: '#D98218',
          500: '#B5670A',
          600: '#8C4E06',
        },
        // Engagement — deep gold, distinct from the turmeric above
        engagement: {
          50: '#F8F1DC',
          400: '#A98A26',
          500: '#8A6C17',
          600: '#6B520F',
        },
        gold: {
          300: '#E3CA85',
          400: '#C9A227',
          // Darker shade for small text on light backgrounds — #C9A227 on white is
          // ~2.6:1 contrast, which fails WCAG AA for text; this passes.
          500: '#8A6C17',
        },
        ink: '#1B1512',
        muted: '#5B5049',
      },
      boxShadow: {
        soft: '0 10px 40px -12px rgba(27, 21, 18, 0.18)',
        card: '0 16px 36px -8px rgba(27, 21, 18, 0.16)',
      },
    },
  },
  plugins: [],
}
