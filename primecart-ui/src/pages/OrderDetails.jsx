import { useEffect, useState } from "react"
import { Link, useParams } from "react-router-dom"
import { getOrder } from "../services/orderService"

function OrderDetails() {
  const { id } = useParams()

  const [order, setOrder] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let cancelled = false
    let refreshTimer
    let refreshAttempts = 0

    async function loadOrder() {
      try {
        const data = await getOrder(id)

        if (cancelled) return

        setOrder(data)

        const isPaymentBeingPrepared = ["PENDING", "INVENTORY_RESERVED"].includes(data.status)

        if (isPaymentBeingPrepared && refreshAttempts < 30) {
          refreshAttempts += 1
          refreshTimer = window.setTimeout(loadOrder, 1000)
        }
      } catch (error) {
        console.error(error)
      } finally {
        if (!cancelled) setLoading(false)
      }
    }

    loadOrder()

    return () => {
      cancelled = true
      window.clearTimeout(refreshTimer)
    }
  }, [id])

  if (loading) {
    return <div className="p-8 text-center">Loading...</div>
  }

  if (!order) {
    return <div className="p-8 text-center">Order not found.</div>
  }

  return (
    <div className="max-w-5xl mx-auto p-6">
      <div className="bg-white shadow rounded-xl p-6">
        <h1 className="text-3xl font-bold mb-6">Order Details</h1>

        <div className="grid grid-cols-2 gap-4 mb-8">
          <div>
            <p className="text-gray-500">Order Number</p>
            <p className="font-semibold">{order.orderNumber}</p>
          </div>

          <div>
            <p className="text-gray-500">Status</p>

            <span className="bg-blue-100 text-blue-700 px-3 py-1 rounded-full">{order.status}</span>
          </div>

          <div>
            <p className="text-gray-500">Order Date</p>
            <p>{new Date(order.createdAt).toLocaleString()}</p>
          </div>

          <div>
            <p className="text-gray-500">Total Amount</p>
            <p className="text-xl font-bold">₹ {order.totalAmount}</p>
          </div>
        </div>

        <h2 className="text-2xl font-semibold mb-4">Items</h2>

        <table className="w-full border-collapse">
          <thead>
            <tr className="border-b">
              <th className="text-left py-3">Product</th>

              <th>Price</th>

              <th>Qty</th>

              <th>Subtotal</th>
            </tr>
          </thead>

          <tbody>
            {order.items.map((item) => (
              <tr key={item.productId} className="border-b">
                <td className="py-4">{item.productName}</td>

                <td className="text-center">₹ {item.price}</td>

                <td className="text-center">{item.quantity}</td>

                <td className="text-center font-semibold">₹ {item.subtotal}</td>
              </tr>
            ))}
          </tbody>
        </table>
        {["PENDING", "INVENTORY_RESERVED"].includes(order.status) && (
          <p className="mt-6 text-gray-600">Preparing your payment...</p>
        )}
        {["CREATED", "PAYMENT_PENDING"].includes(order.status) && (
          <Link to={`/payments/${order.id}`} className="inline-block mt-6 bg-green-600 text-white px-5 py-2 rounded">
            Pay Now
          </Link>
        )}
      </div>
    </div>
  )
}

export default OrderDetails
