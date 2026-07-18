import useOrder from "../hooks/useOrder"
import { Link, useParams } from "react-router-dom"

function OrderDetails() {
  const { id } = useParams()
  const { order, isLoading, isError, error, isFetching } = useOrder(id)

  if (isLoading) {
    return <div className="p-8 text-center">Loading...</div>
  }

  if (isError) {
    return (
      <div className="py-20 text-center">
        <h2 className="text-2xl font-semibold text-red-600">Unable to load order</h2>

        <p className="mt-3 text-gray-500">{error?.message ?? "Something went wrong."}</p>
      </div>
    )
  }
  if (!order) {
    return <div className="p-8 text-center">Order not found.</div>
  }

  return (
    <div className="max-w-5xl mx-auto p-6">
      <div className="bg-white shadow rounded-xl p-6">
        <div className="mb-6 flex items-center justify-between">
          <h1 className="text-3xl font-bold">Order Details</h1>
          {isFetching && <span className="text-sm text-gray-500">Updating...</span>}
        </div>
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
