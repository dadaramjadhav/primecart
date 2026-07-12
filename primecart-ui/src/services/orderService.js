import api from "../api/axios"

export async function getMyOrders() {
  const response = await api.get("/api/orders")
  return response.data
}

export async function getOrder(id) {
  const response = await api.get(`/api/orders/${id}`)
  return response.data
}

export async function createOrder() {
  const response = await api.post("/api/orders")
  return response.data
}
