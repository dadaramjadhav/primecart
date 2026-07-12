import { Link } from "react-router-dom"
import keycloak from "../auth/keycloak"

function Navbar() {
  return (
    <nav className="bg-blue-600 text-white shadow">
      <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-4">
        <Link to="/" className="text-2xl font-bold">
          PrimeCart
        </Link>

        <div className="flex gap-6">
          <Link to="/">Home</Link>

          {keycloak.authenticated ? (
            <>
              <Link to="/products">Products</Link>

              <Link to="/cart">Cart</Link>

              <Link to="/orders">Orders</Link>

              <button
                onClick={() =>
                  keycloak.logout({
                    redirectUri: window.location.origin,
                  })
                }>
                Logout
              </button>
            </>
          ) : (
            <Link to="/login">Login</Link>
          )}
          <span>
            {keycloak.idTokenParsed?.given_name} {keycloak.idTokenParsed?.family_name}
          </span>
        </div>
      </div>
    </nav>
  )
}

export default Navbar
