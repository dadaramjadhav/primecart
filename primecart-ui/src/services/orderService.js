import api from "../api/axios"
import { normalizeServiceError } from "./serviceError"

export async function getMyOrders(page = 0, size = 3) {
  try {
    const response = await api.get("/api/orders", {
      params: { page, size, sort: "createdAt,desc" },
    })
    return response.data
  } catch (error) {
    throw normalizeServiceError(error, "Unable to load orders.")
  }
}

export async function getOrder(id) {
  try {
    const response = await api.get(`/api/orders/${id}`)
    return response.data
  } catch (error) {
    throw normalizeServiceError(error, "Unable to load the order.")
  }
}

export async function createOrder() {
  try {
    const response = await api.post("/api/orders")
    return response.data
  } catch (error) {
    throw normalizeServiceError(error, "Unable to create the order.")
  }
}
