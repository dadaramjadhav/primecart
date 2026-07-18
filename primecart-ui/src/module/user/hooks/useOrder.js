import { useQuery } from "@tanstack/react-query"
import { getOrder } from "../services/orderService"

const PREPARING_PAYMENT_STATUSES = ["PENDING", "INVENTORY_RESERVED"]

function useOrder(orderId) {
  const orderQuery = useQuery({
    queryKey: ["orders", orderId],
    queryFn: () => getOrder(orderId),
    enabled: Boolean(orderId),

    refetchInterval: (query) => {
      const order = query.state.data
      const isPaymentBeingPrepared = PREPARING_PAYMENT_STATUSES.includes(order?.status)

      const reachedMaximumAttempts = query.state.dataUpdateCount >= 30

      if (isPaymentBeingPrepared && !reachedMaximumAttempts) {
        return 1000
      }

      return false
    },
  })

  return {
    order: orderQuery.data ?? null,
    isLoading: orderQuery.isPending,
    isError: orderQuery.isError,
    error: orderQuery.error,
    isFetching: orderQuery.isFetching,
  }
}

export default useOrder
