import keycloak from "./keycloak"

let refreshPromise = null
let refreshIsForced = false

export async function refreshAccessToken({ force = false } = {}) {
  if (!keycloak.authenticated) {
    throw new Error("User is not authenticated.")
  }

  // A refresh is already running.
  if (refreshPromise) {
    const currentRefresh = refreshPromise
    const currentRefreshIsForced = refreshIsForced

    await currentRefresh

    // Reuse the completed refresh when:
    // 1. The caller only wanted proactive refresh, or
    // 2. The current refresh was already forced.
    if (!force || currentRefreshIsForced) {
      return keycloak.token
    }

    // A forced refresh was requested while a proactive refresh
    // was running. Force one refresh after it completes.
    return refreshAccessToken({ force: true })
  }

  const minValidity = force ? -1 : 30

  const currentRefresh = keycloak.updateToken(minValidity).then(() => keycloak.token)

  refreshPromise = currentRefresh
  refreshIsForced = force

  try {
    return await currentRefresh
  } finally {
    // Do not clear a newer refresh operation.
    if (refreshPromise === currentRefresh) {
      refreshPromise = null
      refreshIsForced = false
    }
  }
}
