import useCreateOrder from "../hooks/useCreateOrder"
import { useNavigate } from "react-router-dom"
import { showError } from "../utils/notifications"
import useCart from "../hooks/useCart"

function Cart() {
  const navigate = useNavigate()
  const { cart, isLoading, isError, error, isFetching, retryCart, updateItem, removeItem } = useCart()
  const { createNewOrder, isCreatingOrder } = useCreateOrder()
  async function checkout() {
    try {
      const order = await createNewOrder()

      navigate(`/orders/${order.id}`)
    } catch (error) {
      console.error(error)
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
    return <div>Loading...</div>
  }
  if (isError) {
    return (
      <div className="py-20 text-center">
        <h2 className="text-2xl font-semibold text-red-600">Unable to load cart</h2>

        <p className="mt-3 text-gray-500">{error?.message ?? "Something went wrong."}</p>

        <button
          type="button"
          onClick={() => retryCart()}
          disabled={isFetching}
          className="mt-6 rounded bg-blue-600 px-5 py-2 text-white disabled:opacity-50">
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
      <div className="p-8 text-center">
        <h2 className="text-2xl">Your cart is empty</h2>
      </div>
    )
  }

  return (
    <div className="p-6">
      <h1 className="text-3xl font-bold mb-6">Shopping Cart</h1>

      <div className="space-y-4">
        {cart.items.map((item) => (
          <div key={item.id} className="flex justify-between items-center border rounded p-4">
            <div>
              <h2 className="font-semibold">{item.productName}</h2>

              <p>₹ {item.price}</p>
            </div>

            <div className="flex items-center gap-3">
              <button onClick={() => decrease(item)} className="border px-3">
                -
              </button>

              <span>{item.quantity}</span>

              <button onClick={() => increase(item)} className="border px-3">
                +
              </button>
            </div>

            <div>₹ {item.price * item.quantity}</div>

            <button onClick={() => remove(item.id)} className="bg-red-500   text-white px-3 py-1 rounded">
              Remove
            </button>
          </div>
        ))}
      </div>

      <div className="mt-8 text-right">
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
