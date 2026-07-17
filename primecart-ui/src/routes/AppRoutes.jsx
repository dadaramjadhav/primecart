import { BrowserRouter, Routes, Route } from "react-router-dom"

import MainLayout from "../layouts/MainLayout"

import Home from "../pages/Home"
import Products from "../pages/Products"
import Cart from "../pages/Cart"
import Orders from "../pages/Orders"
import Login from "../pages/Login"
import ProductDetails from "../pages/ProductDetails"
import ProtectedRoute from "./ProtectedRoute"
import OrderDetails from "../pages/OrderDetails"
import Payment from "../pages/Payment"
import NotFound from "../pages/NotFound"

function AppRoutes() {
  return (
    <BrowserRouter>
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
            path="/payments/:orderId"
            element={
              <ProtectedRoute>
                <Payment />
              </ProtectedRoute>
            }
          />
          <Route path="/products/:id" element={<ProductDetails />} />

          <Route path="/orders" element={<Orders />} />

          <Route path="/login" element={<Login />} />
          <Route path="*" element={<NotFound />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default AppRoutes
