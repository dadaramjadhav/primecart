import { useCallback, useEffect, useMemo, useState } from "react"
import keycloak from "../auth/keycloak"
import AuthContext from "./AuthContext"
import { refreshAccessToken } from "../auth/tokenManager"
import { logoutExpiredSession } from "@/auth/sessionManager"

function AuthProvider({ children }) {
  const [authState, setAuthState] = useState(() => ({
    authenticated: Boolean(keycloak.authenticated),
    user: keycloak.tokenParsed ?? keycloak.idTokenParsed ?? null,
  }))

  const syncAuthState = useCallback(() => {
    setAuthState({
      authenticated: Boolean(keycloak.authenticated),
      user: keycloak.tokenParsed ?? keycloak.idTokenParsed ?? null,
    })
  }, [])

  useEffect(() => {
    keycloak.onAuthSuccess = syncAuthState
    keycloak.onAuthRefreshSuccess = syncAuthState
    keycloak.onAuthLogout = syncAuthState

    keycloak.onTokenExpired = async () => {
      try {
        await refreshAccessToken({
          force: true,
        })

        syncAuthState()
      } catch {
        try {
          await logoutExpiredSession()
        } catch {
          window.location.replace("/login?reason=session-expired")
        }
      }
    }
    return () => {
      keycloak.onAuthSuccess = undefined
      keycloak.onAuthRefreshSuccess = undefined
      keycloak.onAuthLogout = undefined
      keycloak.onTokenExpired = undefined
    }
  }, [syncAuthState])

  const login = useCallback((redirectPath = "/") => {
    return keycloak.login({
      redirectUri: `${window.location.origin}${redirectPath}`,
    })
  }, [])

  const logout = useCallback(async () => {
    try {
      await keycloak.logout({
        redirectUri: window.location.origin,
      })
    } catch {
      keycloak.clearToken()
      window.location.replace("/")
    }
  }, [])

  const value = useMemo(
    () => ({
      authenticated: authState.authenticated,
      user: authState.user,
      login,
      logout,
    }),
    [authState, login, logout],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export default AuthProvider
