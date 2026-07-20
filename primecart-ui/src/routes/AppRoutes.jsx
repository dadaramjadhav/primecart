import { BrowserRouter, Route, Routes } from "react-router-dom"
const UserRoutes = lazy(() => import("../module/user/routes/UserRoutes"))
const AdminRoutes = lazy(() => import("../module/admin/routes/AdminRoutes"))
import Login from "../pages/Login"
import { lazy, Suspense } from "react"
function AppRoutes() {
  return (
    <BrowserRouter>
      <Suspense fallback={<div>Loading...</div>}>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/admin/*" element={<AdminRoutes />} />
          <Route path="/*" element={<UserRoutes />} />
        </Routes>
      </Suspense>
    </BrowserRouter>
  )
}

export default AppRoutes
