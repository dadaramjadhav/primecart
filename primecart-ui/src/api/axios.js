import axios from "axios"
import keycloak from "../auth/keycloak"

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
})
api.interceptors.request.use((config) => {
  if (keycloak.token) {
    config.headers.Authorization = `Bearer ${keycloak.token}`
  }

  return config
})

api.interceptors.response.use(
  (response) => {
    return response
  },

  async (error) => {
    const originalRequest = error.config
    const status = error.response?.status

    if (status === 401 && originalRequest && !originalRequest._retry && keycloak.authenticated) {
      originalRequest._retry = true

      try {
        await keycloak.updateToken(-1)

        originalRequest.headers.Authorization = `Bearer ${keycloak.token}`

        return api(originalRequest)
      } catch {
        await keycloak.logout({
          redirectUri: window.location.origin,
        })
      }
    }

    return Promise.reject(error)
  },
)
export default api
