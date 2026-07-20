import { useState } from "react"
import { Link } from "react-router-dom"

import useTheme from "../../../shared/hooks/useTheme"
import useAuth from "../../../shared/hooks/useAuth"

function Navbar() {
  const [isMenuOpen, setIsMenuOpen] = useState(false)
  const [isUserMenuOpen, setIsUserMenuOpen] = useState(false)

  const { authenticated, logout, user, changePassword } = useAuth()
  const { theme, toggleTheme } = useTheme()

  const displayName = user?.given_name || user?.name || user?.preferred_username || "User"

  const email = user?.email || ""
  const initial = displayName.charAt(0).toUpperCase()

  async function handleLogout() {
    setIsUserMenuOpen(false)
    setIsMenuOpen(false)
    await logout()
  }

  async function handleChangePassword() {
    setIsUserMenuOpen(false)
    setIsMenuOpen(false)
    await changePassword()
  }
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
          <Link to="/" onClick={() => setIsUserMenuOpen(false)}>
            Home
          </Link>

          {authenticated ? (
            <>
              <Link to="/products" onClick={() => setIsUserMenuOpen(false)}>
                Products
              </Link>

              <Link to="/cart" onClick={() => setIsUserMenuOpen(false)}>
                Cart
              </Link>

              <Link to="/orders" onClick={() => setIsUserMenuOpen(false)}>
                Orders
              </Link>
            </>
          ) : (
            <Link to="/login">Login</Link>
          )}

          {authenticated && user && (
            <div className="relative">
              <button
                type="button"
                onClick={() => setIsUserMenuOpen((isOpen) => !isOpen)}
                className="flex h-10 w-10 items-center justify-center rounded-full bg-blue-600 font-semibold text-white transition hover:bg-blue-700"
                aria-label="Open user menu"
                aria-expanded={isUserMenuOpen}>
                {initial}
              </button>

              {isUserMenuOpen && (
                <div className="absolute right-0 top-12 z-50 w-64 overflow-hidden rounded-lg border border-slate-200 bg-white shadow-lg dark:border-slate-700 dark:bg-slate-900">
                  <div className="border-b border-slate-200 px-4 py-3 dark:border-slate-700">
                    <p className="text-xs text-slate-500 dark:text-slate-400">Welcome</p>

                    <p className="mt-1 truncate text-sm font-semibold text-slate-900 dark:text-slate-100">
                      {displayName}
                      {email && <span className="ml-1 font-normal text-slate-500 dark:text-slate-400">({email})</span>}
                    </p>
                  </div>
                  <div className="p-2">
                    <Link
                      to="/profile"
                      onClick={() => setIsUserMenuOpen(false)}
                      className="block rounded-md px-3 py-2 text-sm text-slate-700 transition-colors hover:bg-slate-100 dark:text-slate-200 dark:hover:bg-slate-800">
                      Profile
                    </Link>

                    <button
                      type="button"
                      onClick={handleChangePassword}
                      className="block w-full rounded-md px-3 py-2 text-left text-sm text-slate-700 transition-colors hover:bg-slate-100 dark:text-slate-200 dark:hover:bg-slate-800">
                      Change Password
                    </button>

                    <button
                      type="button"
                      onClick={handleLogout}
                      className="block w-full rounded-md px-3 py-2 text-left text-sm text-red-600 transition-colors hover:bg-red-50 dark:text-red-400 dark:hover:bg-red-950/30">
                      Logout
                    </button>
                  </div>
                </div>
              )}
            </div>
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

          {authenticated && user && (
            <div className="mt-2 flex items-center gap-3 border-t border-slate-200 px-3 pt-4 dark:border-slate-700">
              <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-blue-600 font-semibold text-white">
                {initial}
              </div>

              <div className="min-w-0">
                <p className="text-xs text-slate-500 dark:text-slate-400">Welcome</p>

                <p className="truncate text-sm font-semibold">{displayName}</p>
              </div>
            </div>
          )}
          {authenticated && (
            <>
              <Link to="/profile" onClick={() => setIsMenuOpen(false)}>
                Profile
              </Link>

              <button
                type="button"
                onClick={handleChangePassword}
                className="rounded-md px-3 py-2 text-left transition-colors hover:bg-blue-50 hover:text-blue-600 dark:hover:bg-slate-800">
                Change Password
              </button>

              <button type="button" onClick={handleLogout} className="rounded-md px-3 py-2 text-left text-red-600">
                Logout
              </button>
            </>
          )}
        </div>
      )}
    </nav>
  )
}

export default Navbar
