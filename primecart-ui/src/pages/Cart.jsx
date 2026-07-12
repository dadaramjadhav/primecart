import { useEffect, useState } from "react"
import { getCart, removeCartItem, updateCartItem } from "../services/cartService"
import { createOrder } from "../services/orderService"
import { useNavigate } from "react-router-dom"

function Cart() {
  const navigate = useNavigate()
  const [cart, setCart] = useState(null)

  async function checkout() {
    try {
      const order = await createOrder()

      navigate(`/orders/${order.id}`)
    } catch (error) {
      console.error(error)

      alert("Unable to create order.")
    }
  }

  async function loadCart() {
    const data = await getCart()

    setCart(data)
  }

  useEffect(() => {
    async function fetchCart() {
      const data = await getCart()

      setCart(data)
    }

    fetchCart()
  }, [])

  async function increase(item) {
    await updateCartItem(item.id, item.quantity + 1)

    loadCart()
  }

  async function decrease(item) {
    if (item.quantity <= 1) return

    await updateCartItem(item.id, item.quantity - 1)

    loadCart()
  }

  async function remove(itemId) {
    await removeCartItem(itemId)

    loadCart()
  }

  if (!cart) {
    return <div>Loading...</div>
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
          <div
            key={item.id}
            className="
                        flex
                        justify-between
                        items-center
                        border
                        rounded
                        p-4
                        ">
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

            <button
              onClick={() => remove(item.id)}
              className="
                            bg-red-500
                            text-white
                            px-3
                            py-1
                            rounded
                            ">
              Remove
            </button>
          </div>
        ))}
      </div>

      <div
        className="
                mt-8
                text-right
                ">
        <h2 className="text-xl font-bold">Total: ₹ {cart.totalAmount}</h2>

        <button onClick={checkout} className="mt-4 bg-green-600 text-white px-6 py-3 rounded">
          Checkout
        </button>
      </div>
    </div>
  )
}

export default Cart
