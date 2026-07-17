import api from "../api/axios"
import { normalizeServiceError } from "./serviceError"

export async function addToCart(productId, quantity) {
  try {
    const response = await api.post("/api/cart/items", {
      productId,
      quantity,
    })
    return response.data
  } catch (error) {
    throw normalizeServiceError(error, "Unable to add the product to your cart.")
  }
}
export async function getCart() {
  const response = await api.get("/api/cart")
  return response.data
}

export async function clearCart() {
  await api.delete("/api/cart")
}

export async function removeCartItem(itemId) {
  try {
    const response = await api.delete(`/api/cart/items/${itemId}`)
    return response.data
  } catch (error) {
    throw normalizeServiceError(error, "Unable to update the cart quantity.")
  }
}

export async function updateCartItem(itemId, quantity) {
  try {
    const response = await api.put(`/api/cart/items/${itemId}`, {
      quantity,
    })
    return response.data
  } catch (error) {
    throw normalizeServiceError(error, "Unable to update the cart quantity.")
  }
}
