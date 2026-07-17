import { Link } from "react-router-dom"

function Footer() {
  const currentYear = new Date().getFullYear()

  return (
    <footer className="border-t border-slate-200 bg-slate-50 text-slate-700 transition-colors dark:border-slate-700 dark:bg-slate-950 dark:text-slate-300">
      <div className="mx-auto grid max-w-7xl gap-8 px-6 py-10 sm:grid-cols-2 lg:grid-cols-3">
        <div>
          <Link to="/" className="inline-flex items-center text-2xl font-bold text-slate-900 dark:text-white">
            Prime<span className="text-blue-600 dark:text-blue-400">Cart</span>
          </Link>
          <p className="mt-3 max-w-sm text-sm leading-6 text-slate-600 dark:text-slate-400">
            A simple and secure place to discover products, manage your cart, and track every order.
          </p>
        </div>

        <div>
          <h2 className="font-semibold text-slate-900 dark:text-white">Quick links</h2>
          <nav className="mt-4 flex flex-col items-start gap-3 text-sm" aria-label="Footer navigation">
            <Link to="/" className="transition-colors hover:text-blue-400">Home</Link>
            <Link to="/products" className="transition-colors hover:text-blue-400">Products</Link>
            <Link to="/cart" className="transition-colors hover:text-blue-400">Cart</Link>
            <Link to="/orders" className="transition-colors hover:text-blue-400">Orders</Link>
          </nav>
        </div>

        <div className="hidden sm:col-span-2 sm:block lg:col-span-1">
          <h2 className="font-semibold text-slate-900 dark:text-white">Why PrimeCart?</h2>
          <ul className="mt-4 space-y-3 text-sm text-slate-600 dark:text-slate-400">
            <li>Secure checkout and payments</li>
            <li>Easy order tracking</li>
            <li>Responsive shopping experience</li>
          </ul>
        </div>
      </div>

      <div className="border-t border-slate-200 px-6 py-4 text-center text-sm text-slate-500 dark:border-slate-700 dark:text-slate-400">
        © {currentYear} PrimeCart. All rights reserved.
      </div>
    </footer>
  )
}

export default Footer
