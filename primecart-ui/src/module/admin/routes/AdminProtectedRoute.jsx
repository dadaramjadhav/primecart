import { Navigate, useLocation } from "react-router-dom"
import useAuth from "../../../shared/hooks/useAuth"

export default function AdminProtectedRoute({ children }) {
  const { authenticated, user } = useAuth()

  const location = useLocation()
  if (!authenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />
  }

  const realmRoles = user?.realm_access?.roles ?? []

  const clientRoles = user?.resource_access?.[import.meta.env.VITE_KEYCLOAK_CLIENT_ID]?.roles ?? []

  const isAdmin = [...realmRoles, ...clientRoles].some((role) => role.toLowerCase() === "admin")

  if (!isAdmin) {
    return <Navigate to="/" replace />
  }
  return children
}
