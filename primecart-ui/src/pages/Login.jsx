import useAuth from "../hooks/useAuth"
function Login() {
  const { login } = useAuth()

  return (
    <div className="min-h-[80vh] flex items-center justify-center bg-gray-100 px-4">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-xl p-8">
        <div className="text-center mb-8">
          <h1 className="text-4xl font-bold text-blue-600">PrimeCart</h1>

          <p className="mt-3 text-gray-600">Welcome back! Sign in to continue shopping.</p>
        </div>

        <button
          onClick={login}
          className="w-full bg-blue-600 hover:bg-blue-700 transition-colors text-white font-semibold py-3 rounded-lg">
          Login with Keycloak
        </button>

        <div className="mt-6 border-t pt-4 text-center text-sm text-gray-500">
          Secure authentication powered by Keycloak
        </div>
      </div>
    </div>
  )
}

export default Login
