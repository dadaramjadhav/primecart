import keycloak from "./keycloak"

let logoutPromise = null

export function logoutExpiredSession() {
  if (!logoutPromise) {
    const redirectUri = `${window.location.origin}/login?reason=session-expired`

    logoutPromise = keycloak.logout({ redirectUri }).finally(() => {
      logoutPromise = null
    })
  }

  return logoutPromise
}
