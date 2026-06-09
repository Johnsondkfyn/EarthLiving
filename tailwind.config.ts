import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./app/**/*.{js,ts,jsx,tsx,mdx}",
    "./components/**/*.{js,ts,jsx,tsx,mdx}",
    "./data/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        earth: {
          background: "#0B0F14",
          card: "#121820",
          accent: "#22C55E",
          text: "#FFFFFF",
          muted: "#94A3B8",
        },
      },
      boxShadow: {
        "green-glow": "0 0 40px rgba(34, 197, 94, 0.22)",
      },
    },
  },
  plugins: [],
};

export default config;
