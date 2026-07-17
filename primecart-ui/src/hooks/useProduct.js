import { useQuery } from "@tanstack/react-query"
import { getProduct } from "../services/productService"

function useProduct(productId) {
  const productQuery = useQuery({
    queryKey: ["products", productId],
    queryFn: () => getProduct(productId),
    enabled: Boolean(productId),
    staleTime: 30_000,
  })

  return {
    product: productQuery.data ?? null,
    isLoading: productQuery.isPending,
    isError: productQuery.isError,
    error: productQuery.error,
    isFetching: productQuery.isFetching,
  }
}

export default useProduct