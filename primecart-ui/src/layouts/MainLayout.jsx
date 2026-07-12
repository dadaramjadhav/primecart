import { Outlet } from "react-router-dom"
import Navbar from "../components/Navbar"
import Footer from "../components/Footer"

function MainLayout() {
  return (
    <div className="flex min-h-screen flex-col">
      <Navbar />

      <main className="flex-1 p-8">
        <Outlet />
      </main>

      <Footer />
    </div>
  )
}

export default MainLayout
