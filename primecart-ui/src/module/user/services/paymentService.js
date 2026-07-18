import api from "../../../api/axios"

export async function getPayment(orderId) {
  const response = await api.get(`/api/payments/order/${orderId}`)

  return response.data
}

export async function paymentSuccess(paymentId) {
  const response = await api.put(`/api/payments/${paymentId}/success`)

  return response.data
}

export async function paymentFailed(paymentId) {
  const response = await api.put(`/api/payments/${paymentId}/failed`)

  return response.data
}
