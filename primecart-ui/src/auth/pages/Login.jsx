import { useLocation } from "react-router-dom"
import useAuth from "../../shared/hooks/useAuth"
import { useSearchParams } from "react-router-dom"

function Login() {
  const { login } = useAuth()
  const location = useLocation()
  const redirectPath = location.state?.from?.pathname ?? "/"
  const [searchParams] = useSearchParams()
  const sessionExpired = searchParams.get("reason") === "session-expired"
  function handleUserLogin() {
    login(redirectPath)
  }

  function handleAdminLogin() {
    login("/admin")
  }
  return (
    <div>
      {sessionExpired && <p className="mb-4 text-sm text-destructive">Your session expired. Please sign in again.</p>}
      <div className="min-h-[80vh] flex items-center justify-center bg-gray-100 px-4">
        <div className="w-full max-w-md bg-white rounded-2xl shadow-xl p-8">
          <div className="text-center mb-8">
            <h1 className="text-4xl font-bold text-blue-600">PrimeCart</h1>

            <p className="mt-3 text-gray-600">Welcome back! Sign in to continue shopping.</p>
          </div>

          <button
            type="button"
            onClick={handleUserLogin}
            className="w-full rounded-lg bg-blue-600 py-3 font-semibold text-white hover:bg-blue-700">
            User Login
          </button>
          <button
            type="button"
            onClick={handleAdminLogin}
            className="mt-3 w-full rounded-lg bg-slate-800 py-3 font-semibold text-white hover:bg-slate-900">
            Admin Login
          </button>
          <div className="mt-6 border-t pt-4 text-center text-sm text-gray-500">
            Secure authentication powered by Keycloak
          </div>
        </div>
      </div>
    </div>
  )
}

export default Login
