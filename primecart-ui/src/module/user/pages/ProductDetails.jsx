import { useParams } from "react-router-dom"
import useCart from "../hooks/useCart"
import useAuth from "../../../shared/hooks/useAuth"
import useProduct from "../hooks/useProduct"
import { showSuccess } from "../../../shared/utils/notifications"

function ProductDetails() {
  const { id } = useParams()
  const { product, isLoading, isError, error, isFetching } = useProduct(id)
  const { authenticated } = useAuth()
  const { cart, addItem, updateItem, removeItem } = useCart(authenticated)
  const cartItem = cart?.items.find((item) => item.productId === product?.id)

  async function handleAddToCart() {
    await addItem(product.id)

    showSuccess("Item added to cart")
  }

  async function increaseQuantity() {
    await updateItem(cartItem.id, cartItem.quantity + 1)
  }

  async function decreaseQuantity() {
    if (cartItem.quantity === 1) {
      await removeItem(cartItem.id)
    } else {
      await updateItem(cartItem.id, cartItem.quantity - 1)
    }
  }
  if (isLoading) {
    return <div className="p-8 text-center">Loading product...</div>
  }

  if (isError) {
    return <div className="p-8 text-center text-red-600">{error?.message ?? "Unable to load product."}</div>
  }

  if (!product) {
    return null
  }
  return (
    <div>
      <h1>{product.name}</h1>
      {isFetching && <span className="text-sm text-gray-500">Updating...</span>}
      <img src={product.imageUrl} alt={product.name} width={300} />

      <h2>₹ {product.price}</h2>

      <p>{product.description}</p>

      {cartItem ? (
        <div className="flex items-center gap-3">
          <button type="button" onClick={decreaseQuantity} className="h-9 w-9 rounded border">
            -
          </button>
          <span>{cartItem.quantity}</span>
          <button type="button" onClick={increaseQuantity} className="h-9 w-9 rounded border">
            +
          </button>
        </div>
      ) : (
        <button onClick={handleAddToCart} className="bg-blue-600 text-white px-5 py-2 rounded">
          Add to Cart
        </button>
      )}
    </div>
  )
}

export default ProductDetails
