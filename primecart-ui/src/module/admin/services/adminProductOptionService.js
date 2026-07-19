import api from "@/api/axios"

export async function getAdminProductOptions() {
  const [categoriesResponse, brandsResponse] = await Promise.all([api.get("/api/categories"), api.get("/api/brands")])

  return {
    categories: categoriesResponse.data,
    brands: brandsResponse.data,
  }
}
