import { Outlet } from "react-router-dom"
import Navbar from "../../module/user/components/Navbar"
import Footer from "../../module/user/components/Footer"

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
