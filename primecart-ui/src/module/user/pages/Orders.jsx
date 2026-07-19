import { useState } from "react"
import { Link } from "react-router-dom"
import useOrders from "../hooks/useOrders"

const PAGE_SIZE = Number(import.meta.env.VITE_ORDERS_PAGE_SIZE) || 3

function scrollToTop() {
  window.scrollTo({
    top: 0,
    behavior: "smooth",
  })
}
function getPaginationItems(currentPage, totalPages) {
  if (totalPages <= 6) {
    return Array.from({ length: totalPages }, (_, index) => index)
  }

  const lastPage = totalPages - 1

  if (currentPage <= 3) {
    return [0, 1, 2, 3, 4, "ellipsis-right", lastPage]
  }

  if (currentPage >= lastPage - 3) {
    return [0, "ellipsis-left", lastPage - 4, lastPage - 3, lastPage - 2, lastPage - 1, lastPage]
  }

  return [0, "ellipsis-left", currentPage - 1, currentPage, currentPage + 1, "ellipsis-right", lastPage]
}

function Orders() {
  const [currentPage, setCurrentPage] = useState(0)
  const { orders, totalPages, totalElements, isLoading, isError, error, isFetching, isPlaceholderData } = useOrders(
    currentPage,
    PAGE_SIZE,
  )

  function handlePreviousPage() {
    setCurrentPage((previousPage) => Math.max(previousPage - 1, 0))
    scrollToTop()
  }

  function handleNextPage() {
    setCurrentPage((previousPage) => Math.min(previousPage + 1, totalPages - 1))
    scrollToTop()
  }
  function handlePageChange(pageNumber) {
    setCurrentPage(pageNumber)
    scrollToTop()
  }

  if (isLoading) {
    return <div className="p-8 text-center text-muted-foreground">Loading orders...</div>
  }

  if (isError) {
    return (
      <div className="rounded-xl border bg-card px-6 py-20 text-center text-card-foreground">
        <h2 className="text-2xl font-semibold text-destructive">Unable to load orders</h2>

        <p className="mt-3 text-muted-foreground">{error?.message ?? "Something went wrong."}</p>
      </div>
    )
  }

  if (orders.length === 0 && currentPage === 0) {
    return (
      <div className="rounded-xl border bg-card py-20 text-center text-card-foreground">
        <h2 className="text-3xl font-semibold">No Orders Found</h2>

        <p className="mt-3 text-muted-foreground">Start shopping to place your first order.</p>

        <Link to="/products" className="mt-6 inline-block rounded-lg bg-primary px-6 py-3 text-primary-foreground hover:bg-primary/80">
          Shop Now
        </Link>
      </div>
    )
  }

  return (
    <div className="mx-auto max-w-6xl p-6 text-foreground">
      <div className="mb-8 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">My Orders</h1>

          <p className="mt-2 text-muted-foreground">Total orders: {totalElements}</p>
        </div>

        {isFetching && <span className="text-sm text-muted-foreground">Updating...</span>}
      </div>
      <div className={`space-y-5 ${isPlaceholderData ? "opacity-60" : ""}`}>
        {orders.map((order) => (
          <div key={order.id} className="rounded-xl border bg-card p-6 text-card-foreground shadow-sm">
            <div className="flex justify-between items-center">
              <div>
                <p className="font-semibold">Order #{order.orderNumber}</p>

                <p className="text-sm text-muted-foreground">{new Date(order.createdAt).toLocaleString()}</p>
              </div>

              <span className="rounded-full bg-primary/10 px-3 py-1 text-sm text-primary">{order.status}</span>
            </div>

            <div className="mt-5 flex justify-between items-center">
              <h2 className="text-xl font-bold">₹ {order.totalAmount}</h2>

              <Link to={`/orders/${order.id}`} className="font-medium text-primary hover:underline">
                View Details →
              </Link>
            </div>
          </div>
        ))}
      </div>
      {totalPages > 1 && (
        <div className="mt-10">
          <div className="flex items-center justify-between gap-3 sm:hidden">
            <button
              type="button"
              onClick={handlePreviousPage}
              disabled={currentPage === 0}
              className="rounded-lg border bg-background px-3 py-2 text-sm hover:bg-muted disabled:cursor-not-allowed disabled:opacity-40">
              Previous
            </button>

            <span className="whitespace-nowrap text-sm text-muted-foreground">
              Page {currentPage + 1} of {totalPages}
            </span>

            <button
              type="button"
              onClick={handleNextPage}
              disabled={currentPage === totalPages - 1}
              className="rounded-lg border bg-background px-3 py-2 text-sm hover:bg-muted disabled:cursor-not-allowed disabled:opacity-40">
              Next
            </button>
          </div>

          <div className="hidden flex-wrap items-center justify-center gap-2 sm:flex">
            <button
              type="button"
              onClick={handlePreviousPage}
              disabled={currentPage === 0}
              className="rounded-lg border bg-background px-4 py-2 hover:bg-muted disabled:cursor-not-allowed disabled:opacity-40">
              Previous
            </button>

            {getPaginationItems(currentPage, totalPages).map((item) =>
              typeof item === "string" ? (
                <span key={item} className="px-1 text-muted-foreground" aria-hidden="true">
                  …
                </span>
              ) : (
                <button
                  type="button"
                  key={item}
                  onClick={() => handlePageChange(item)}
                  aria-label={`Go to page ${item + 1}`}
                  aria-current={currentPage === item ? "page" : undefined}
                  className={`h-10 w-10 rounded-lg border ${
                    currentPage === item
                      ? "border-primary bg-primary text-primary-foreground"
                      : "bg-background text-foreground hover:bg-muted"
                  }`}>
                  {item + 1}
                </button>
              ),
            )}

            <button
              type="button"
              onClick={handleNextPage}
              disabled={currentPage === totalPages - 1}
              className="rounded-lg border bg-background px-4 py-2 hover:bg-muted disabled:cursor-not-allowed disabled:opacity-40">
              Next
            </button>
          </div>
        </div>
      )}
    </div>
  )
}

export default Orders
