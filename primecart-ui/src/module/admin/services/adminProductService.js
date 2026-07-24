import api from "@/api/axios"
import axios from "axios"

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
export async function createProductImageUpload(file) {
  const response = await api.post("/api/product-images/upload-url", {
    fileName: file.name,
    contentType: file.type,
    fileSize: file.size,
  })

  return response.data
}

//Use plain Axios for S3 so the Keycloak authorization header isn’t added.
export async function uploadProductImage(uploadUrl, file) {
  await axios.put(uploadUrl, file, {
    headers: {
      "Content-Type": file.type,
      "x-amz-meta-original-filename": file.name,
    },
  })
}
