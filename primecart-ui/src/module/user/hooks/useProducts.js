import { getProducts } from "../services/productService"
import { useInfiniteQuery } from "@tanstack/react-query"

function useProducts(pageSize = 8) {
  const productsQuery = useInfiniteQuery({
    queryKey: ["products", "infinite", pageSize],
    queryFn: ({ pageParam }) => getProducts(pageParam, pageSize),
    initialPageParam: 0,
    getNextPageParam: (lastPage) => {
      const nextPage = lastPage.number + 1

      return nextPage < lastPage.totalPages ? nextPage : undefined
    },
    staleTime: 10_000,
  })
  const products = productsQuery.data?.pages.flatMap((page) => page.content) ?? []
  return {
    products,
    isLoading: productsQuery.isPending,
    isError: productsQuery.isError,
    error: productsQuery.error,
    isFetching: productsQuery.isFetching,

    fetchNextPage: productsQuery.fetchNextPage,
    hasNextPage: productsQuery.hasNextPage,
    isFetchingNextPage: productsQuery.isFetchingNextPage,
  }
}

export default useProducts
