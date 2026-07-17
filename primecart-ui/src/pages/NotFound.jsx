import { Link } from "react-router-dom"

function NotFound() {
  return (
    <div className="flex min-h-[60vh] items-center justify-center px-4">
      <div className="text-center">
        <p className="text-7xl font-bold text-blue-600">404</p>

        <h1 className="mt-4 text-3xl font-semibold">Page not found</h1>

        <p className="mt-3 text-slate-500 dark:text-slate-400">
          The page you requested does not exist or may have moved.
        </p>

        <div className="mt-7 flex justify-center gap-3">
          <Link to="/" className="rounded-lg bg-blue-600 px-5 py-2 text-white hover:bg-blue-700">
            Go Home
          </Link>

          <Link
            to="/products"
            className="rounded-lg border border-slate-300 px-5 py-2 hover:bg-slate-100 dark:border-slate-700 dark:hover:bg-slate-800">
            View Products
          </Link>
        </div>
      </div>
    </div>
  )
}

export default NotFound
