import { keepPreviousData, useQuery } from "@tanstack/react-query"

import { getMyOrders } from "../services/orderService"

function useOrders(currentPage, pageSize = 3) {
  const ordersQuery = useQuery({
    queryKey: ["my-orders", currentPage, pageSize],
    queryFn: () => getMyOrders(currentPage, pageSize),
    placeholderData: keepPreviousData,
    staleTime: 30_000,
  })

  return {
    orders: ordersQuery.data?.content ?? [],
    totalPages: ordersQuery.data?.totalPages ?? 0,
    totalElements: ordersQuery.data?.totalElements ?? 0,
    isLoading: ordersQuery.isPending,
    isError: ordersQuery.isError,
    error: ordersQuery.error,
    isFetching: ordersQuery.isFetching,
    isPlaceholderData: ordersQuery.isPlaceholderData,
  }
}

export default useOrders
