import { useMutation, useQueryClient } from "@tanstack/react-query"
import { updateAdminProduct } from "../services/adminProductService"

function useUpdateAdminProduct() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: updateAdminProduct,

    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: ["admin", "products"],
      })

      queryClient.invalidateQueries({
        queryKey: ["admin", "products", variables.productId],
      })
    },
  })
}

export default useUpdateAdminProduct
