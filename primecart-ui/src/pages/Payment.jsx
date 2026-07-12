import { useEffect, useState } from "react"
import { useNavigate, useParams } from "react-router-dom"

import { getPayment, paymentSuccess, paymentFailed } from "../services/paymentService"

function Payment() {
  const { orderId } = useParams()

  const navigate = useNavigate()

  const [payment, setPayment] = useState(null)

  useEffect(() => {
    async function loadPayment() {
      try {
        const data = await getPayment(orderId)
        setPayment(data)
      } catch (error) {
        console.error(error)
      }
    }
    loadPayment()
  }, [orderId])

  async function payNow() {
    try {
      await paymentSuccess(payment.id)

      navigate(`/orders/${orderId}`)
    } catch (error) {
      console.error(error)
    }
  }

  async function failPayment() {
    try {
      await paymentFailed(payment.id)

      navigate(`/orders/${orderId}`)
    } catch (error) {
      console.error(error)
    }
  }

  if (!payment) {
    return <div className="text-center mt-20">Loading...</div>
  }

  return (
    <div className="max-w-xl mx-auto mt-10">
      <div className="bg-white shadow-lg rounded-xl p-8">
        <h1 className="text-3xl font-bold mb-8">Payment</h1>

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
          <button onClick={payNow} className="flex-1 bg-green-600 text-white py-3 rounded-lg hover:bg-green-700">
            Pay Now
          </button>

          <button onClick={failPayment} className="flex-1 bg-red-600 text-white py-3 rounded-lg hover:bg-red-700">
            Fail Payment
          </button>
        </div>
      </div>
    </div>
  )
}

export default Payment
