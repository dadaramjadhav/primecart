import { useQuery } from "@tanstack/react-query"
import { getAdminProduct } from "../services/adminProductService"

function useAdminProduct(productId) {
  return useQuery({
    queryKey: ["admin", "products", productId],

    queryFn: () => getAdminProduct(productId),

    enabled: Boolean(productId),

    staleTime: 10_000,
  })
}

export default useAdminProduct