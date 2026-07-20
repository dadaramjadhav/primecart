import { defineConfig } from "vite"
import react from "@vitejs/plugin-react"
import tailwindcss from "@tailwindcss/vite"
import { fileURLToPath, URL } from "node:url"
import fs from "node:fs"

const certificatePath = fileURLToPath(new URL("../infra/tls/localhost-cert.pem", import.meta.url))

const privateKeyPath = fileURLToPath(new URL("../infra/tls/localhost-key.pem", import.meta.url))

const developmentCsp = [
  "default-src 'self'",
  "script-src 'self'",
  "style-src 'self' 'unsafe-inline'",
  "img-src 'self' https: data:",
  "font-src 'self' data:",
  "connect-src 'self' https://localhost:8181 https://localhost:8443 wss://localhost:5173",
  "frame-src https://localhost:8443",
  "object-src 'none'",
  "base-uri 'self'",
  "form-action 'self' https://localhost:8443",
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
    https: {
      cert: fs.readFileSync(certificatePath),
      key: fs.readFileSync(privateKeyPath),
    },
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
