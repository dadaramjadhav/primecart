import { useQuery } from "@tanstack/react-query"
import { getCustomerProfile } from "../services/customerService"

function useCustomer() {
  const customerQuery = useQuery({
    queryKey: ["customers", "profile"],
    queryFn: () => getCustomerProfile(),
    staleTime: 10_000,
  })

  return {
    customer: customerQuery.data ?? null,
    isLoading: customerQuery.isPending,
    isError: customerQuery.isError,
    error: customerQuery.error,
    isFetching: customerQuery.isFetching,
    refetch: customerQuery.refetch,
  }
}

export default useCustomer
