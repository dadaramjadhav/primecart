
import { Navigate } from "react-router-dom"
import useAuth from "../hooks/useAuth"

function ProtectedRoute({ children }) {
  const {authenticated}= useAuth()
  if (!authenticated) {
    return <Navigate to="/login" />
  }

  return children
}

export default ProtectedRoute
