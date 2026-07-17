import { useCallback, useEffect, useMemo, useState } from "react"
import keycloak from "../auth/keycloak"
import AuthContext from "./AuthContext"

function AuthProvider({ children }) {
  const [authState, setAuthState] = useState(() => ({
    authenticated: Boolean(keycloak.authenticated),
    user: keycloak.idTokenParsed ?? null,
    token: keycloak.token ?? null,
  }))

  const syncAuthState = useCallback(() => {
    setAuthState({
      authenticated: Boolean(keycloak.authenticated),
      user: keycloak.idTokenParsed ?? null,
      token: keycloak.token ?? null,
    })
  }, [])

  useEffect(() => {
    keycloak.onAuthSuccess = syncAuthState
    keycloak.onAuthRefreshSuccess = syncAuthState
    keycloak.onAuthLogout = syncAuthState

    keycloak.onTokenExpired = async () => {
      try {
        await keycloak.updateToken(30)
        syncAuthState()
      } catch {
        await keycloak.logout({
          redirectUri: window.location.origin,
        })
      }
    }

    return () => {
      keycloak.onAuthSuccess = undefined
      keycloak.onAuthRefreshSuccess = undefined
      keycloak.onAuthLogout = undefined
      keycloak.onTokenExpired = undefined
    }
  }, [syncAuthState])

  const login = useCallback(() => {
    return keycloak.login({
      redirectUri: window.location.origin,
    })
  }, [])

  const logout = useCallback(() => {
    return keycloak.logout({
      redirectUri: window.location.origin,
    })
  }, [])

  const value = useMemo(
    () => ({
      authenticated: authState.authenticated,
      user: authState.user,
      token: authState.token,
      login,
      logout,
    }),
    [authState, login, logout],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export default AuthProvider
