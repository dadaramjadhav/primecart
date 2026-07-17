import { useMutation, useQueryClient } from "@tanstack/react-query"

import { createOrder } from "../services/orderService"

function useCreateOrder() {
  const queryClient = useQueryClient()

  const createOrderMutation = useMutation({
    mutationFn: createOrder,

    onSuccess: async (createdOrder) => {
      queryClient.setQueryData(["orders", String(createdOrder.id)], createdOrder)

      await Promise.all([
        queryClient.invalidateQueries({
          queryKey: ["my-orders"],
        }),

        queryClient.invalidateQueries({
          queryKey: ["cart"],
        }),
      ])
    },
  })

  return {
    createNewOrder: () => createOrderMutation.mutateAsync(),

    isCreatingOrder: createOrderMutation.isPending,
    createOrderError: createOrderMutation.error,
  }
}

export default useCreateOrder
