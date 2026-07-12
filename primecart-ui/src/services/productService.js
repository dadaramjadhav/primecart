import api from "../api/axios"

export const getProducts = async () => {
  const response = await api.get("/api/products")

  return response.data
}

export const getProduct = async (id) => {
  const response = await api.get(`/api/products/${id}`)

  return response.data
}
