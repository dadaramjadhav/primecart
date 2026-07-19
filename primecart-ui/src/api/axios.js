import axios from "axios"

import keycloak from "../auth/keycloak"
import { refreshAccessToken } from "../auth/tokenManager"
import { SessionExpiredError } from "../auth/errors"
import { logoutExpiredSession } from "../auth/sessionManager"

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
})
api.interceptors.request.use(async (config) => {
  if (!keycloak.authenticated) {
    return config
  }
  const token = await refreshAccessToken()

  config.headers.Authorization = `Bearer ${token}`

  return config
})

// error handling
api.interceptors.response.use(
  (response) => response,

  async (error) => {
    const originalRequest = error.config
    const status = error.response?.status

    const shouldRetry = status === 401 && originalRequest && !originalRequest._retry && keycloak.authenticated

    if (!shouldRetry) {
      return Promise.reject(error)
    }

    originalRequest._retry = true

    try {
      const token = await refreshAccessToken({
        force: true,
      })

      originalRequest.headers.Authorization = `Bearer ${token}`

      return api(originalRequest)
    } catch (refreshError) {
      const sessionError = new SessionExpiredError({
        cause: refreshError,
      })

      try {
        await logoutExpiredSession()
      } catch {
        window.location.replace("/login?reason=session-expired")
      }

      return Promise.reject(sessionError)
    }
  },
)
export default api
