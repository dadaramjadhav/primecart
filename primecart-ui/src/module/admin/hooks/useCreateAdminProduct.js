import { useMutation, useQueryClient } from "@tanstack/react-query"
import { createAdminProduct } from "../services/adminProductService"

function useCreateAdminProduct() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: createAdminProduct,

    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["admin", "products"],
      })
    },
  })
}

export default useCreateAdminProduct
