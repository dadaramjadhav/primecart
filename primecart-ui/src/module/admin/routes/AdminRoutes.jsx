import { Route, Routes } from "react-router-dom"
import AdminProtectedRoute from "./AdminProtectedRoute"
import AdminDashboard from "../pages/AdminDashboard"
import AdminLayout from "../layouts/AdminLayout"
import AdminProducts from "../pages/AdminProducts"
import AdminCreateProduct from "../pages/AdminCreateProduct"
import AdminEditProduct from "../pages/AdminEditProduct"

export default function AdminRoutes() {
  return (
    <Routes>
      <Route index element={<AdminDashboard />} />
      <Route
        element={
          <AdminProtectedRoute>
            <AdminLayout />
          </AdminProtectedRoute>
        }>
        <Route
          path="products"
          element={
            <AdminProtectedRoute>
              <AdminProducts />
            </AdminProtectedRoute>
          }
        />
        <Route
          path="products/:productId/edit"
          element={
            <AdminProtectedRoute>
              <AdminEditProduct />
            </AdminProtectedRoute>
          }
        />
        <Route path="products/new" element={<AdminCreateProduct />} />
      </Route>
    </Routes>
  )
}
