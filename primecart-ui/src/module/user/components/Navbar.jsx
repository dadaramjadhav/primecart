import { useState } from "react"
import { Link } from "react-router-dom"

import useTheme from "../../../shared/hooks/useTheme"
import useAuth from "../../../shared/hooks/useAuth"

function Navbar() {
  const [isMenuOpen, setIsMenuOpen] = useState(false)
  const { authenticated, logout, user } = useAuth()
  const { theme, toggleTheme } = useTheme()
  return (
    <nav className="relative border-b border-slate-200 bg-white text-slate-700 shadow-sm transition-colors dark:border-slate-700 dark:bg-slate-900 dark:text-slate-100">
      <div className="mx-auto flex h-20 max-w-7xl items-center justify-between py-0 pl-3 pr-14 sm:h-16 sm:pl-6 sm:pr-16">
        <Link to="/" className="flex h-20 shrink-0 items-center sm:h-full">
          <img
            src={theme === "dark" ? "/dark-logo.png" : "/light-logo.png"}
            alt="PrimeCart logo"
            className="h-full w-auto max-w-none object-contain"
          />
        </Link>

        <button
          type="button"
          className="ml-auto rounded-md p-2 text-slate-700 transition-colors hover:bg-blue-50 hover:text-blue-600 sm:hidden"
          aria-label={isMenuOpen ? "Close navigation menu" : "Open navigation menu"}
          aria-expanded={isMenuOpen}
          aria-controls="mobile-navigation"
          onClick={() => setIsMenuOpen((isOpen) => !isOpen)}>
          {isMenuOpen ? (
            <svg className="h-7 w-7" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path strokeLinecap="round" d="M6 6l12 12M18 6 6 18" />
            </svg>
          ) : (
            <svg className="h-7 w-7" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path strokeLinecap="round" d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          )}
        </button>

        <div className="ml-auto hidden items-center gap-6 sm:flex [&_a]:transition-colors [&_a:hover]:text-blue-600">
          <Link to="/">Home</Link>

          {authenticated ? (
            <>
              <Link to="/products">Products</Link>

              <Link to="/cart">Cart</Link>

              <Link to="/orders">Orders</Link>
            </>
          ) : (
            <Link to="/login">Login</Link>
          )}

          {user && <span className="text-sm text-slate-500 dark:text-slate-400">Welcome, {user.given_name}</span>}

          {authenticated && (
            <button className="cursor-pointer transition-colors hover:text-blue-600" onClick={logout}>
              Logout
            </button>
          )}
        </div>

        <button
          type="button"
          onClick={toggleTheme}
          className="absolute right-2 top-1/2 -translate-y-1/2 cursor-pointer p-1"
          aria-label="Toggle theme">
          <img src={theme === "light" ? "/dark-mode.png" : "/light-mode.png"} alt="" className="h-6 w-6 dark:invert" />
        </button>
      </div>
      {isMenuOpen && (
        <div
          id="mobile-navigation"
          className="flex flex-col border-t border-slate-200 px-4 py-3 sm:hidden [&_a]:rounded-md [&_a]:px-3 [&_a]:py-2 [&_a]:transition-colors [&_a:hover]:bg-blue-50 [&_a:hover]:text-blue-600">
          <Link to="/" onClick={() => setIsMenuOpen(false)}>
            Home
          </Link>

          {authenticated ? (
            <>
              <Link to="/products" onClick={() => setIsMenuOpen(false)}>
                Products
              </Link>
              <Link to="/cart" onClick={() => setIsMenuOpen(false)}>
                Cart
              </Link>
              <Link to="/orders" onClick={() => setIsMenuOpen(false)}>
                Orders
              </Link>
            </>
          ) : (
            <Link to="/login" onClick={() => setIsMenuOpen(false)}>
              Login
            </Link>
          )}

          {user && (
            <span className="border-t border-slate-100 px-3 pt-3 text-sm text-slate-500 dark:border-slate-700 dark:text-slate-400">
              Welcome, {user.given_name}
            </span>
          )}
          {authenticated && (
            <button
              className="cursor-pointer rounded-md px-3 py-2 text-left transition-colors hover:bg-blue-50 hover:text-blue-600"
              onClick={logout}>
              Logout
            </button>
          )}
        </div>
      )}
    </nav>
  )
}

export default Navbar
