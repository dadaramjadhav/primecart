import useOrder from "../hooks/useOrder"
import { Link, useParams } from "react-router-dom"

function OrderDetails() {
  const { id } = useParams()
  const { order, isLoading, isError, error, isFetching } = useOrder(id)

  if (isLoading) {
    return <div className="p-8 text-center text-muted-foreground">Loading order...</div>
  }

  if (isError) {
    return (
      <div className="rounded-xl border bg-card px-6 py-20 text-center text-card-foreground">
        <h2 className="text-2xl font-semibold text-destructive">Unable to load order</h2>

        <p className="mt-3 text-muted-foreground">{error?.message ?? "Something went wrong."}</p>
      </div>
    )
  }
  if (!order) {
    return <div className="rounded-xl border bg-card p-8 text-center text-muted-foreground">Order not found.</div>
  }

  return (
    <div className="mx-auto max-w-5xl p-6 text-foreground">
      <div className="rounded-xl border bg-card p-6 text-card-foreground shadow-sm">
        <div className="mb-6 flex items-center justify-between">
          <h1 className="text-3xl font-bold">Order Details</h1>
          {isFetching && <span className="text-sm text-muted-foreground">Updating...</span>}
        </div>
        <div className="mb-8 grid gap-4 sm:grid-cols-2">
          <div>
            <p className="text-muted-foreground">Order Number</p>
            <p className="font-semibold">{order.orderNumber}</p>
          </div>

          <div>
            <p className="text-muted-foreground">Status</p>

            <span className="inline-block rounded-full bg-primary/10 px-3 py-1 text-primary">{order.status}</span>
          </div>

          <div>
            <p className="text-muted-foreground">Order Date</p>
            <p>{new Date(order.createdAt).toLocaleString()}</p>
          </div>

          <div>
            <p className="text-muted-foreground">Total Amount</p>
            <p className="text-xl font-bold">₹ {order.totalAmount}</p>
          </div>
        </div>

        <h2 className="mb-4 text-2xl font-semibold">Items</h2>

        <div className="overflow-x-auto rounded-lg border">
          <table className="w-full min-w-lg border-collapse">
            <thead className="bg-muted/50">
              <tr className="border-b">
                <th className="px-4 py-3 text-left">Product</th>

                <th className="px-4 py-3">Price</th>

                <th className="px-4 py-3">Qty</th>

                <th className="px-4 py-3">Subtotal</th>
              </tr>
            </thead>

            <tbody>
              {order.items.map((item) => (
                <tr key={item.productId} className="border-b last:border-b-0 hover:bg-muted/30">
                  <td className="px-4 py-4">{item.productName}</td>

                  <td className="px-4 text-center">₹ {item.price}</td>

                  <td className="px-4 text-center">{item.quantity}</td>

                  <td className="px-4 text-center font-semibold">₹ {item.subtotal}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {["PENDING", "INVENTORY_RESERVED"].includes(order.status) && (
          <p className="mt-6 text-muted-foreground">Preparing your payment...</p>
        )}
        {["CREATED", "PAYMENT_PENDING", "PAYMENT_FAILED"].includes(order.status) && (
          <Link
            to={`/payments/${order.id}`}
            className={`mt-6 inline-block rounded-lg px-5 py-2 text-white ${
              order.status === "PAYMENT_FAILED"
                ? "bg-orange-600 hover:bg-orange-700"
                : "bg-green-600 hover:bg-green-700"
            }`}>
            {order.status === "PAYMENT_FAILED" ? "Retry Payment" : "Pay Now"}
          </Link>
        )}
      </div>
    </div>
  )
}

export default OrderDetails
