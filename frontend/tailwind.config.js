/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        background: '#0B0E11',
        surface: {
          DEFAULT: '#12161A',
          secondary: '#181D23',
          card: '#12161A',
          border: '#1F242C',
          input: '#15191E',
        },
        monetra: {
          green: '#00C853',
          'green-hover': '#00B048',
          'green-light': '#10B981',
          'green-dim': 'rgba(0, 200, 83, 0.15)',
          red: '#EF4444',
          'red-dim': 'rgba(239, 68, 68, 0.15)',
          orange: '#F59E0B',
          yellow: '#EAB308',
          pink: '#EC4899',
          purple: '#8B5CF6',
          cyan: '#06B6D4',
        },
        text: {
          primary: '#FFFFFF',
          secondary: '#8B949E',
          muted: '#6E7681',
        }
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', '-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'Roboto', 'sans-serif'],
      },
      borderRadius: {
        'card': '10px',
        'btn': '8px',
        'badge': '6px',
      }
    },
  },
  plugins: [],
}
