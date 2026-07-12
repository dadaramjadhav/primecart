import { useEffect, useState } from "react"
import { Link } from "react-router-dom"
import { getMyOrders } from "../services/orderService"

function Orders() {
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    async function loadOrders() {
      try {
        const data = await getMyOrders()
        setOrders(data.content)
      } catch (error) {
        console.error(error)
      } finally {
        setLoading(false)
      }
    }

    loadOrders()
  }, [])

  if (loading) {
    return <div className="p-8 text-center">Loading orders...</div>
  }

  if (orders.length === 0) {
    return (
      <div className="text-center py-20">
        <h2 className="text-3xl font-semibold">No Orders Found</h2>

        <p className="text-gray-500 mt-3">Start shopping to place your first order.</p>

        <Link to="/products" className="inline-block mt-6 bg-blue-600 text-white px-6 py-3 rounded-lg">
          Shop Now
        </Link>
      </div>
    )
  }

  return (
    <div className="max-w-6xl mx-auto p-6">
      <h1 className="text-3xl font-bold mb-8">My Orders</h1>

      <div className="space-y-5">
        {orders.map((order) => (
          <div key={order.id} className="bg-white shadow rounded-xl border p-6">
            <div className="flex justify-between items-center">
              <div>
                <p className="font-semibold">Order #{order.orderNumber}</p>

                <p className="text-sm text-gray-500">{new Date(order.createdAt).toLocaleString()}</p>
              </div>

              <span className="px-3 py-1 rounded-full bg-blue-100 text-blue-700 text-sm">{order.status}</span>
            </div>

            <div className="mt-5 flex justify-between items-center">
              <h2 className="text-xl font-bold">₹ {order.totalAmount}</h2>

              <Link to={`/orders/${order.id}`} className="text-blue-600 font-medium hover:underline">
                View Details →
              </Link>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

export default Orders
