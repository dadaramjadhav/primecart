import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"

import { clearCart } from "../services/cartService"
import { getPayment, paymentFailed, paymentSuccess, retryPayment } from "../services/paymentService"

import { shouldRetryQuery } from "../../../api/queryRetry"

function usePayment(orderId) {
  const queryClient = useQueryClient()

  const paymentQuery = useQuery({
    queryKey: ["payments", orderId],
    queryFn: () => getPayment(orderId),
    enabled: Boolean(orderId),
    retry: (failureCount, error) => {
      const status = error?.status ?? error?.response?.status

      if (status === 404) {
        return failureCount < 10
      }

      return shouldRetryQuery(failureCount, error)
    },

    retryDelay: (attemptIndex) => Math.min(250 * 2 ** attemptIndex, 2000),
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

  const retryMutation = useMutation({
    mutationFn: retryPayment,
    onSuccess: refreshRelatedData,
  })

  return {
    payment: paymentQuery.data ?? null,
    isLoading: paymentQuery.isPending,
    isError: paymentQuery.isError,
    error: paymentQuery.error,
    isFetching: paymentQuery.isFetching,
    isRetrying: retryMutation.isPending,
    completePayment: (paymentId) => successMutation.mutateAsync(paymentId),
    failPayment: (paymentId) => failureMutation.mutateAsync(paymentId),
    retryFailedPayment: (paymentId) => retryMutation.mutateAsync(paymentId),
    isSubmitting: successMutation.isPending || failureMutation.isPending || retryMutation.isPending,
  }
}

export default usePayment
