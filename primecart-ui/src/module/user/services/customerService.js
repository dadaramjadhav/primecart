import api from "../../../api/axios"

export async function getCustomerProfile() {
  const response = await api.get("/api/customers/me")
  return response.data
}
export async function updateCustomerProfile(profileData) {
  const response = await api.put("/api/customers/me", profileData)

  return response.data
}
