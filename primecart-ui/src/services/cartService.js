import api from "../api/axios"

export async function addToCart(productId, quantity) {
  const response = await api.post("/api/cart/items", {
    productId,
    quantity,
  })
  return response.data
}
export async function getCart() {
  const response = await api.get("/api/cart")
  return response.data
}

export async function clearCart() {
  await api.delete("/api/cart")
}

export async function removeCartItem(itemId) {
  const response = await api.delete(`/api/cart/items/${itemId}`)
  return response.data
}

export async function updateCartItem(itemId, quantity) {
  const response = await api.put(`/api/cart/items/${itemId}`, {
    quantity,
  })
  return response.data
}
