import keycloak from "../auth/keycloak"
import { Navigate } from "react-router-dom"

function ProtectedRoute({ children }) {
  if (!keycloak.authenticated) {
    return <Navigate to="/login" />
  }

  return children
}

export default ProtectedRoute
