import keycloak from "../auth/keycloak"

function Home() {
  return (
    <div className="text-center">
      <h1 className="text-4xl font-bold text-blue-600">Welcome to PrimeCart</h1>

      <p className="mt-4 text-gray-600">Your one-stop online shopping platform.</p>
      <div>
        <pre>Access token: {keycloak.token}</pre>
        <pre>Id token: {keycloak.idToken}</pre>
      </div>
    </div>
  )
}

export default Home
