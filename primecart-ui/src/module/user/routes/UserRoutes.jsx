import { Routes, Route } from "react-router-dom"

import MainLayout from "../../../shared/layouts/MainLayout"

import Home from "../pages/Home"
import Products from "../pages/Products"
import Cart from "../pages/Cart"
import Orders from "../pages/Orders"
import ProductDetails from "../pages/ProductDetails"
import ProtectedRoute from "./ProtectedRoute"
import OrderDetails from "../pages/OrderDetails"
import Payment from "../pages/Payment"
import NotFound from "../pages/NotFound"
import Profile from "../pages/Profile"

function UserRoutes() {
  return (
    <Routes>
      <Route element={<MainLayout />}>
        <Route path="/" element={<Home />} />
        <Route
          path="/products"
          element={
            <ProtectedRoute>
              <Products />
            </ProtectedRoute>
          }
        />

        <Route
          path="/cart"
          element={
            <ProtectedRoute>
              <Cart />
            </ProtectedRoute>
          }
        />
        <Route
          path="/orders/:id"
          element={
            <ProtectedRoute>
              <OrderDetails />
            </ProtectedRoute>
          }
        />
        <Route
          path="/orders"
          element={
            <ProtectedRoute>
              <Orders />
            </ProtectedRoute>
          }
        />
        <Route
          path="/payments/:orderId"
          element={
            <ProtectedRoute>
              <Payment />
            </ProtectedRoute>
          }
        />
        <Route
          path="/products/:id"
          element={
            <ProtectedRoute>
              <ProductDetails />{" "}
            </ProtectedRoute>
          }
        />
        <Route
          path="/profile"
          element={
            <ProtectedRoute>
              <Profile />
            </ProtectedRoute>
          }
        />

        <Route path="*" element={<NotFound />} />
      </Route>
    </Routes>
  )
}

export default UserRoutes
