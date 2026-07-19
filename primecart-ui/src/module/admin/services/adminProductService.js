import api from "@/api/axios"

export async function getAdminProducts(page = 0, size = 10) {
  const response = await api.get("/api/products", {
    params: { page, size },
  })
  return response.data
}

export async function createAdminProduct(productData) {
  const response = await api.post("/api/products", productData)

  return response.data
}

export async function deleteAdminProduct(productId) {
  await api.delete(`/api/products/${productId}`)
}

export async function getAdminProduct(productId) {
  const response = await api.get(`/api/products/${productId}`)

  return response.data
}

export async function updateAdminProduct({ productId, productData }) {
  const response = await api.put(`/api/products/${productId}`, productData)

  return response.data
}
