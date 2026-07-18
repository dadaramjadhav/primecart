import { BrowserRouter } from "react-router-dom"
import UserRoutes from "../module/user/routes/UserRoutes"

function AppRoutes() {
  return (
    <BrowserRouter>
      <UserRoutes />
    </BrowserRouter>
  )
}

export default AppRoutes
