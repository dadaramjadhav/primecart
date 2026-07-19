import { Link, Outlet } from "react-router-dom"
import { Button, buttonVariants } from "@/shared/ui/button"
import useAuth from "@/shared/hooks/useAuth"

function AdminLayout() {
  const { logout } = useAuth()
  return (
    <div className="min-h-screen bg-slate-100">
      <header className="bg-slate-900 px-6 py-4 text-white">
        <div className="flex items-center justify-between">
          <Link to="/admin" className="text-xl font-bold">
            PrimeCart Admin
          </Link>

          <div className="flex items-center gap-3">
            <Link
              to="/"
              className={buttonVariants({
                variant: "secondary",
              })}>
              Go to Store
            </Link>

            <Button variant="destructive" className="cursor-pointer" onClick={logout}>
              Logout
            </Button>
          </div>
        </div>
      </header>

      <div className="flex">
        <aside className="min-h-[calc(100vh-64px)] w-64 bg-white p-6 shadow">
          <nav className="flex flex-col gap-4">
            <Link to="/admin">Dashboard</Link>
            <Link to="/admin/products">Products</Link>
            <Link to="/admin/orders">Orders</Link>
          </nav>
        </aside>

        <main className="flex-1 p-8">
          <Outlet />
        </main>
      </div>
    </div>
  )
}

export default AdminLayout
