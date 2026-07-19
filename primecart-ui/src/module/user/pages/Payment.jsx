import usePayment from "../hooks/usePayment"
import { useNavigate, useParams } from "react-router-dom"
import { logSafeError } from "@/shared/utils/safeLogger"

function Payment() {
  const { orderId } = useParams()

  const navigate = useNavigate()

  const { payment, completePayment, failPayment, isError, isFetching, isLoading, error, isSubmitting } =
    usePayment(orderId)
  async function payNow() {
    try {
      await completePayment(payment.id)

      navigate(`/orders/${orderId}`)
    } catch (error) {
      logSafeError("Payment creation failed", error)
    }
  }

  async function handleFailedPayment() {
    try {
      await failPayment(payment.id)

      navigate(`/orders/${orderId}`)
    } catch (error) {
      logSafeError("Payment failure update failed", error)
    }
  }

  if (isLoading) {
    return <div className="text-center mt-20">Loading...</div>
  }
  if (isError) {
    return (
      <div className="py-20 text-center">
        <h2 className="text-2xl font-semibold text-red-600">Unable to load payment</h2>

        <p className="mt-3 text-gray-500">{error?.message ?? "Something went wrong."}</p>
      </div>
    )
  }
  if (!payment) {
    return <div className="mt-20 text-center">Payment not found.</div>
  }
  return (
    <div className="max-w-xl mx-auto mt-10">
      <div className="bg-white shadow-lg rounded-xl p-8">
        <h1 className="text-3xl font-bold mb-8">Payment</h1>
        {isFetching && <span className="text-sm text-gray-500">Updating...</span>}
        <div className="space-y-4">
          <div className="flex justify-between">
            <span>Payment Number</span>
            <span>{payment.paymentNumber}</span>
          </div>

          <div className="flex justify-between">
            <span>Amount</span>
            <span className="font-bold text-xl">₹ {payment.amount}</span>
          </div>

          <div className="flex justify-between">
            <span>Status</span>
            <span>{payment.status}</span>
          </div>

          <div className="flex justify-between">
            <span>Method</span>
            <span>{payment.method}</span>
          </div>
        </div>

        <div className="flex gap-4 mt-10">
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
          </button>{" "}
        </div>
      </div>
    </div>
  )
}

export default Payment
