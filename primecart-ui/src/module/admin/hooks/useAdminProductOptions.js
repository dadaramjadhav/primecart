import { useQuery } from "@tanstack/react-query"

import { getAdminProductOptions } from "../services/adminProductOptionService"

function useAdminProductOptions() {
  return useQuery({
    queryKey: ["admin", "product-options"],
    queryFn: getAdminProductOptions,
    staleTime: 5 * 60_000,
  })
}

export default useAdminProductOptions
