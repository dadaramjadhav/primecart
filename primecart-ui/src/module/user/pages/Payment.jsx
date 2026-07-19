import usePayment from "../hooks/usePayment"
import { useNavigate, useParams } from "react-router-dom"
import { logSafeError } from "@/shared/utils/safeLogger"
import { showError, showSuccess } from "@/shared/utils/notifications"

function Payment() {
  const { orderId } = useParams()

  const navigate = useNavigate()

  const {
    payment,
    completePayment,
    failPayment,
    retryFailedPayment,
    isError,
    isFetching,
    isLoading,
    error,
    isSubmitting,
    isRetrying,
  } = usePayment(orderId)
  async function payNow() {
    try {
      await completePayment(payment.id)
      showSuccess("Payment completed successfully.")

      navigate(`/orders/${orderId}`)
    } catch (error) {
      logSafeError("Payment creation failed", error)
      showError(error, "Unable to complete payment.")
    }
  }

  async function handleFailedPayment() {
    try {
      await failPayment(payment.id)
      showSuccess("Payment marked as failed.")

      navigate(`/orders/${orderId}`)
    } catch (error) {
      logSafeError("Payment failure update failed", error)
      showError(error, "Unable to update payment.")
    }
  }

  async function handleRetryPayment() {
    try {
      await retryFailedPayment(payment.id)

      showSuccess("Payment is ready to retry.")
    } catch (error) {
      logSafeError("Payment retry failed", error)

      showError(error, "Unable to retry payment.")
    }
  }
  if (isLoading) {
    return <div className="mt-20 text-center text-muted-foreground">Loading payment...</div>
  }
  if (isError) {
    return (
      <div className="rounded-xl border bg-card px-6 py-20 text-center text-card-foreground">
        <h2 className="text-2xl font-semibold text-destructive">Unable to load payment</h2>

        <p className="mt-3 text-muted-foreground">{error?.message ?? "Something went wrong."}</p>
      </div>
    )
  }
  if (!payment) {
    return <div className="mt-20 rounded-xl border bg-card p-8 text-center text-muted-foreground">Payment not found.</div>
  }
  return (
    <div className="mx-auto mt-10 max-w-xl text-foreground">
      <div className="rounded-xl border bg-card p-8 text-card-foreground shadow-lg">
        <div className="mb-8 flex items-center justify-between">
          <h1 className="text-3xl font-bold">Payment</h1>
          {isFetching && <span className="text-sm text-muted-foreground">Updating...</span>}
        </div>
        <div className="divide-y rounded-lg border">
          <div className="flex justify-between gap-4 p-4">
            <span className="text-muted-foreground">Payment Number</span>
            <span className="text-right font-medium">{payment.paymentNumber}</span>
          </div>

          <div className="flex justify-between gap-4 p-4">
            <span className="text-muted-foreground">Amount</span>
            <span className="text-xl font-bold">₹ {payment.amount}</span>
          </div>

          <div className="flex items-center justify-between gap-4 p-4">
            <span className="text-muted-foreground">Status</span>
            <span
              className={`rounded-full px-3 py-1 text-sm font-medium ${
                payment.status === "FAILED"
                  ? "bg-destructive/10 text-destructive"
                  : payment.status === "SUCCESS"
                    ? "bg-green-600/10 text-green-600 dark:text-green-400"
                    : "bg-primary/10 text-primary"
              }`}>
              {payment.status}
            </span>
          </div>

          <div className="flex justify-between gap-4 p-4">
            <span className="text-muted-foreground">Method</span>
            <span className="font-medium">{payment.method}</span>
          </div>
        </div>
        {payment.status === "PENDING" && (
          <div className="mt-10 flex gap-4">
            <button
              type="button"
              onClick={payNow}
              disabled={isSubmitting}
              className="flex-1 rounded-lg bg-green-600 py-3 text-white hover:bg-green-700 disabled:cursor-not-allowed disabled:opacity-50">
              {isSubmitting ? "Processing..." : "Pay Now"}
            </button>

            <button
              type="button"
              onClick={handleFailedPayment}
              disabled={isSubmitting}
              className="flex-1 rounded-lg bg-red-600 py-3 text-white hover:bg-red-700 disabled:cursor-not-allowed disabled:opacity-50">
              {isSubmitting ? "Processing..." : "Fail Payment"}
            </button>
          </div>
        )}

        {payment.status === "FAILED" && (
          <button
            type="button"
            onClick={handleRetryPayment}
            disabled={isRetrying}
            className="mt-10 w-full rounded-lg bg-orange-600 py-3 text-white hover:bg-orange-700 disabled:cursor-not-allowed disabled:opacity-50">
            {isRetrying ? "Retrying..." : "Retry Payment"}
          </button>
        )}
      </div>
    </div>
  )
}

export default Payment
