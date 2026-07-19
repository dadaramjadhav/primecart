import useCreateOrder from "../hooks/useCreateOrder"
import { useNavigate } from "react-router-dom"
import { showError } from "../../../shared/utils/notifications"
import useCart from "../hooks/useCart"
import { logSafeError } from "@/shared/utils/safeLogger"

function Cart() {
  const navigate = useNavigate()
  const { cart, isLoading, isError, error, isFetching, retryCart, updateItem, removeItem } = useCart()
  const { createNewOrder, isCreatingOrder } = useCreateOrder()
  async function checkout() {
    try {
      const order = await createNewOrder()

      navigate(`/orders/${order.id}`)
    } catch (error) {
      logSafeError("Cart update failed", error)
      showError(error, "Unable to update your cart.")
    }
  }

  async function increase(item) {
    await updateItem(item.id, item.quantity + 1)
  }

  async function decrease(item) {
    if (item.quantity <= 1) return

    await updateItem(item.id, item.quantity - 1)
  }

  async function remove(itemId) {
    await removeItem(itemId)
  }

  if (isLoading) {
    return <div className="py-20 text-center text-muted-foreground">Loading cart...</div>
  }
  if (isError) {
    return (
      <div className="rounded-xl border bg-card px-6 py-20 text-center text-card-foreground">
        <h2 className="text-2xl font-semibold text-destructive">Unable to load cart</h2>

        <p className="mt-3 text-muted-foreground">{error?.message ?? "Something went wrong."}</p>

        <button
          type="button"
          onClick={() => retryCart()}
          disabled={isFetching}
          className="mt-6 rounded-lg bg-primary px-5 py-2 text-primary-foreground hover:bg-primary/80 disabled:opacity-50">
          {isFetching ? "Retrying..." : "Try Again"}
        </button>
      </div>
    )
  }
  if (!cart) {
    return null
  }
  if (cart.items.length === 0) {
    return (
      <div className="rounded-xl border bg-card p-8 text-center text-card-foreground">
        <h2 className="text-2xl">Your cart is empty</h2>
        <p className="mt-2 text-muted-foreground">Add a product to begin your order.</p>
      </div>
    )
  }

  return (
    <div className="mx-auto max-w-6xl p-6 text-foreground">
      <h1 className="mb-6 text-3xl font-bold">Shopping Cart</h1>

      <div className="space-y-4">
        {cart.items.map((item) => (
          <div
            key={item.id}
            className="flex flex-col gap-4 rounded-xl border bg-card p-4 text-card-foreground shadow-sm sm:flex-row sm:items-center sm:justify-between">
            <div>
              <h2 className="font-semibold">{item.productName}</h2>

              <p className="text-sm text-muted-foreground">₹ {item.price} each</p>
            </div>

            <div className="flex items-center gap-3">
              <button
                type="button"
                onClick={() => decrease(item)}
                className="h-9 w-9 rounded-lg border bg-background hover:bg-muted">
                -
              </button>

              <span className="min-w-6 text-center font-medium">{item.quantity}</span>

              <button
                type="button"
                onClick={() => increase(item)}
                className="h-9 w-9 rounded-lg border bg-background hover:bg-muted">
                +
              </button>
            </div>

            <div className="font-semibold">₹ {item.price * item.quantity}</div>

            <button
              type="button"
              onClick={() => remove(item.id)}
              className="rounded-lg bg-destructive/10 px-3 py-2 text-destructive hover:bg-destructive/20">
              Remove
            </button>
          </div>
        ))}
      </div>

      <div className="mt-8 rounded-xl border bg-card p-6 text-right text-card-foreground">
        <h2 className="text-xl font-bold">Total: ₹ {cart.totalAmount}</h2>

        <button
          type="button"
          onClick={checkout}
          disabled={isCreatingOrder}
          className="mt-4 rounded bg-green-600 px-6 py-3 text-white hover:bg-green-700 disabled:cursor-not-allowed disabled:opacity-50">
          {isCreatingOrder ? "Creating order..." : "Checkout"}
        </button>
      </div>
    </div>
  )
}

export default Cart
