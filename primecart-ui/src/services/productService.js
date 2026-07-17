import api from "../api/axios"
import { normalizeServiceError } from "./serviceError"

export const getProducts = async (page = 0, size = 8) => {
  try {
    const response = await api.get("/api/products", { params: { page, size } })

    return response.data
  } catch (error) {
    throw normalizeServiceError(error, "Unable to load products.")
  }
}

export const getProduct = async (id) => {
  try {
    const response = await api.get(`/api/products/${id}`)

    return response.data
  } catch (error) {
    throw normalizeServiceError(error, "Unable to load the product.")
  }
}
