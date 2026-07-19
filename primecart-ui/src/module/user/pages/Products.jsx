import { Link, useNavigate } from "react-router-dom"
import { showSuccess } from "../../../shared/utils/notifications"
import { logSafeError } from "@/shared/utils/safeLogger"
import useCart from "../hooks/useCart"
import useProducts from "../hooks/useProducts"
import { useEffect, useRef } from "react"

function Products() {
  const navigate = useNavigate()
  const { cart, addItem, updateItem, removeItem } = useCart()
  const { products, isLoading, isError, error, isFetching, fetchNextPage, hasNextPage, isFetchingNextPage } =
    useProducts()

  const loadMoreRef = useRef(null)
  useEffect(() => {
    const loadMoreElement = loadMoreRef.current

    if (!loadMoreElement) {
      return
    }

    const observer = new IntersectionObserver(
      (entries) => {
        const firstEntry = entries[0]

        if (firstEntry.isIntersecting && hasNextPage && !isFetchingNextPage) {
          fetchNextPage()
        }
      },
      {
        rootMargin: "0px",
        threshold: 0.5,
      },
    )

    observer.observe(loadMoreElement)

    return () => {
      observer.disconnect()
    }
  }, [fetchNextPage, hasNextPage, isFetchingNextPage])

  function findCartItem(productId) {
    return cart?.items.find((item) => item.productId === productId)
  }

  async function increaseQuantity(productId) {
    const cartItem = findCartItem(productId)

    if (cartItem) {
      await updateItem(cartItem.id, cartItem.quantity + 1)
    } else {
      await addItem(productId)
      showSuccess("Item added to cart")
    }
  }

  async function decreaseQuantity(productId) {
    const cartItem = findCartItem(productId)

    if (!cartItem) return

    if (cartItem.quantity === 1) {
      await removeItem(cartItem.id)
    } else {
      await updateItem(cartItem.id, cartItem.quantity - 1)
    }
  }

  async function checkoutProduct(productId) {
    try {
      if (!findCartItem(productId)) {
        await addItem(productId)
      }

      navigate("/cart")
    } catch (error) {
      logSafeError("Unable to prepare product checkout", error)
    }
  }

  if (isLoading) {
    return <div className="p-8 text-center text-muted-foreground">Loading products...</div>
  }

  if (isError) {
    return (
      <div className="rounded-xl border bg-card p-8 text-center text-destructive">
        {error?.message ?? "Unable to load products."}
      </div>
    )
  }
  return (
    <div className="mx-auto max-w-7xl text-foreground">
      <h1 className="mb-8 text-3xl font-bold">Products</h1>
      {isFetching && !isFetchingNextPage && <span className="text-sm text-muted-foreground">Updating...</span>}
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
        {products.map((product) => {
          const cartItem = findCartItem(product.id)

          return (
            <div key={product.id} className="rounded-xl border bg-card p-4 text-card-foreground shadow-sm">
              <Link to={`/products/${product.id}`}>
                <img src={product.imageUrl} alt={product.name} className="aspect-square w-full rounded-lg object-cover" />
              </Link>

              <h2 className="mt-4 text-xl font-semibold">
                {product.name} ({product.id})
              </h2>

              <p className="mt-2 text-muted-foreground">{product.description}</p>

              <p className="mt-3 text-lg font-bold text-blue-600">₹{product.price}</p>

              <div className="mt-4 flex flex-wrap items-center gap-3">
                {cartItem ? (
                  <div className="flex items-center gap-3">
                    <button
                      type="button"
                      onClick={() => decreaseQuantity(product.id)}
                      className="h-9 w-9 rounded-lg border bg-background hover:bg-muted">
                      -
                    </button>
                    <span className="min-w-6 text-center font-medium">{cartItem.quantity}</span>
                    <button
                      type="button"
                      onClick={() => increaseQuantity(product.id)}
                      className="h-9 w-9 rounded-lg border bg-background hover:bg-muted">
                      +
                    </button>
                  </div>
                ) : (
                  <button
                    type="button"
                    onClick={() => increaseQuantity(product.id)}
                    className="rounded bg-blue-600 px-4 py-2 text-white hover:bg-blue-700">
                    Add to Cart
                  </button>
                )}

                <button
                  type="button"
                  onClick={() => checkoutProduct(product.id)}
                  className="rounded bg-green-600 px-4 py-2 text-white hover:bg-green-700">
                  Buy Now
                </button>
              </div>
            </div>
          )
        })}
      </div>
      <div ref={loadMoreRef} className="py-8 text-center text-sm text-muted-foreground">
        {isFetchingNextPage
          ? "Loading more products..."
          : hasNextPage
            ? "Scroll for more products"
            : products.length > 0
              ? "You have reached the end"
              : null}
      </div>
    </div>
  )
}

export default Products
