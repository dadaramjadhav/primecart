import { defineConfig } from "vite"
import react from "@vitejs/plugin-react"
import tailwindcss from "@tailwindcss/vite"
import { fileURLToPath, URL } from "node:url"

const developmentCsp = [
  "default-src 'self'",
  "script-src 'self'",
  "style-src 'self' 'unsafe-inline'",
  "img-src 'self' https: data:",
  "font-src 'self' data:",
  "connect-src 'self' http://localhost:8181 http://localhost:8080 ws://localhost:5173",
  "frame-src http://localhost:8080",
  "object-src 'none'",
  "base-uri 'self'",
  "form-action 'self' http://localhost:8080",
  "frame-ancestors 'none'",
].join("; ")

export default defineConfig({
  plugins: [react(), tailwindcss()],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
  server: {
    headers: {
      "Content-Security-Policy-Report-Only": developmentCsp,
    },
  },

  preview: {
    headers: {
      "Content-Security-Policy-Report-Only": developmentCsp,
    },
  },
})
