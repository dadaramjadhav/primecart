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
    return <div className="p-8 text-center text-muted-foreground">Loading product...</div>
  }

  if (isError) {
    return (
      <div className="rounded-xl border bg-card p-8 text-center text-destructive">
        {error?.message ?? "Unable to load product."}
      </div>
    )
  }

  if (!product) {
    return (
      <div className="rounded-xl border bg-card p-8 text-center text-muted-foreground">
        Product not found.
      </div>
    )
  }
  return (
    <div className="mx-auto max-w-5xl py-8 text-foreground">
      <div className="grid gap-8 rounded-xl border bg-card p-6 text-card-foreground shadow-sm md:grid-cols-2 md:p-8">
        <div className="flex min-h-80 items-center justify-center overflow-hidden rounded-lg bg-muted p-6">
          <img
            src={product.imageUrl}
            alt={product.name}
            className="max-h-96 w-full object-contain"
          />
        </div>

        <div className="flex flex-col justify-center">
          <div className="flex items-start justify-between gap-4">
            <h1 className="text-3xl font-bold tracking-tight">{product.name}</h1>
            {isFetching && <span className="text-sm text-muted-foreground">Updating...</span>}
          </div>

          <h2 className="mt-5 text-2xl font-bold text-primary">₹ {product.price}</h2>

          <p className="mt-5 leading-7 text-muted-foreground">{product.description}</p>

          <div className="mt-8">
            {cartItem ? (
              <div className="flex items-center gap-3">
                <button
                  type="button"
                  onClick={decreaseQuantity}
                  aria-label="Decrease quantity"
                  className="h-10 w-10 rounded-md border bg-background text-foreground transition-colors hover:bg-accent hover:text-accent-foreground">
                  −
                </button>
                <span className="min-w-8 text-center font-semibold">{cartItem.quantity}</span>
                <button
                  type="button"
                  onClick={increaseQuantity}
                  aria-label="Increase quantity"
                  className="h-10 w-10 rounded-md border bg-background text-foreground transition-colors hover:bg-accent hover:text-accent-foreground">
                  +
                </button>
              </div>
            ) : (
              <button
                type="button"
                onClick={handleAddToCart}
                className="rounded-md bg-primary px-5 py-2.5 font-medium text-primary-foreground transition-colors hover:bg-primary/90">
                Add to Cart
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}

export default ProductDetails
