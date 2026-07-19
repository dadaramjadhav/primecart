import { keepPreviousData, useQuery } from "@tanstack/react-query"
import { getAdminProducts } from "../services/adminProductService"

function useAdminProducts(page, pageSize = 10) {
  return useQuery({
    queryKey: ["admin", "products", page, pageSize],

    queryFn: () => getAdminProducts(page, pageSize),

    placeholderData: keepPreviousData,

    staleTime: 30_000,
  })
}

export default useAdminProducts
