import { useMutation, useQueryClient } from "@tanstack/react-query"
import { deleteAdminProduct } from "../services/adminProductService"

function useDeleteAdminProduct() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: deleteAdminProduct,

    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["admin", "products"],
      })
    },
  })
}

export default useDeleteAdminProduct
