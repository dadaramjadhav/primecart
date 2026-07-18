import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"

import { clearCart } from "../services/cartService"
import { getPayment, paymentFailed, paymentSuccess } from "../services/paymentService"

function usePayment(orderId) {
  const queryClient = useQueryClient()

  const paymentQuery = useQuery({
    queryKey: ["payments", orderId],
    queryFn: () => getPayment(orderId),
    enabled: Boolean(orderId),
  })

  async function refreshRelatedData() {
    await Promise.all([
      queryClient.invalidateQueries({
        queryKey: ["payments", orderId],
      }),
      queryClient.invalidateQueries({
        queryKey: ["orders", orderId],
      }),
      queryClient.invalidateQueries({
        queryKey: ["cart"],
      }),
    ])
  }

  const successMutation = useMutation({
    mutationFn: async (paymentId) => {
      const updatedPayment = await paymentSuccess(paymentId)

      await clearCart()

      return updatedPayment
    },
    onSuccess: refreshRelatedData,
  })

  const failureMutation = useMutation({
    mutationFn: paymentFailed,
    onSuccess: refreshRelatedData,
  })

  return {
    payment: paymentQuery.data ?? null,
    isLoading: paymentQuery.isPending,
    isError: paymentQuery.isError,
    error: paymentQuery.error,
    isFetching: paymentQuery.isFetching,

    completePayment: (paymentId) => successMutation.mutateAsync(paymentId),

    failPayment: (paymentId) => failureMutation.mutateAsync(paymentId),

    isSubmitting: successMutation.isPending || failureMutation.isPending,
  }
}

export default usePayment
