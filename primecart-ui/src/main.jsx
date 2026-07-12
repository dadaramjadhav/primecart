import { StrictMode } from "react"
import { createRoot } from "react-dom/client"

import App from "./App"
import "./index.css"
import keycloak from "./auth/keycloak"

keycloak
  .init({
    onLoad: "check-sso",
    checkLoginIframe: false,
    pkceMethod: "S256",
  })
  .then(() => {
    createRoot(document.getElementById("root")).render(
      <StrictMode>
        <App />
      </StrictMode>,
    )
  })
  .catch(console.error)
