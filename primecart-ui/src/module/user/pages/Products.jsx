import { Link } from "react-router-dom"
import { showSuccess } from "../../../shared/utils/notifications"
import useCart from "../hooks/useCart"
import useProducts from "../hooks/useProducts"
import { useEffect, useRef } from "react"

function Products() {
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
  if (isLoading) {
    return <div className="p-8 text-center">Loading products...</div>
  }

  if (isError) {
    return <div className="p-8 text-center text-red-600">{error?.message ?? "Unable to load products."}</div>
  }
  return (
    <div className="mx-auto max-w-7xl">
      <h1 className="mb-8 text-3xl font-bold">Products</h1>
      {isFetching && !isFetchingNextPage && <span className="text-sm text-gray-500">Updating...</span>}
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
        {products.map((product) => {
          const cartItem = findCartItem(product.id)

          return (
            <div key={product.id} className="rounded-lg border bg-white p-4 shadow">
              <Link to={`/products/${product.id}`}>
                <img src={product.imageUrl} alt={product.name} />
              </Link>

              <h2 className="text-xl font-semibold">{product.name}</h2>

              <p className="mt-2 text-gray-500">{product.description}</p>

              <p className="mt-3 text-lg font-bold text-blue-600">₹{product.price}</p>

              {cartItem ? (
                <div className="mt-4 flex items-center gap-3">
                  <button type="button" onClick={() => decreaseQuantity(product.id)} className="h-9 w-9 rounded border">
                    -
                  </button>
                  <span>{cartItem.quantity}</span>
                  <button type="button" onClick={() => increaseQuantity(product.id)} className="h-9 w-9 rounded border">
                    +
                  </button>
                </div>
              ) : (
                <button
                  type="button"
                  onClick={() => increaseQuantity(product.id)}
                  className="mt-4 rounded bg-blue-600 px-4 py-2 text-white hover:bg-blue-700">
                  Add to Cart
                </button>
              )}
            </div>
          )
        })}
      </div>
      <div ref={loadMoreRef} className="py-8 text-center text-sm text-gray-500">
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
