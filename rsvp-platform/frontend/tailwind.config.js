/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      fontFamily: {
        display: ['"Fraunces"', 'serif'],
        body: ['"Inter"', 'sans-serif'],
      },
      colors: {
        ivory: '#faf6ee',
        parchment: '#f2e9d8',
        maroon: {
          50: '#fbf1f0',
          100: '#f3d9d6',
          400: '#a83a34',
          500: '#7d211d',
          600: '#5c1815',
          700: '#3f100e',
        },
        gold: {
          300: '#e9c98c',
          400: '#d4a94f',
          500: '#b8862f',
        },
        ink: '#241f1a',
      },
      boxShadow: {
        soft: '0 10px 40px -12px rgba(60, 30, 20, 0.25)',
      },
    },
  },
  plugins: [],
}
