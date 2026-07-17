import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { addToCart, getCart, removeCartItem, updateCartItem } from "../services/cartService"
import { toast } from "react-toastify"
const CART_QUERY_KEY = ["cart"]

function useCart(enabled = true) {
  const queryClient = useQueryClient()
  function handleMutationError(error) {
    toast.error(error?.message ?? "Unable to update your cart.")
  }
  const cartQuery = useQuery({
    queryKey: CART_QUERY_KEY,
    queryFn: getCart,
    enabled,
  })

  function refreshCart() {
    return queryClient.invalidateQueries({ queryKey: CART_QUERY_KEY })
  }

  const addMutation = useMutation({
    mutationFn: ({ productId, quantity }) => addToCart(productId, quantity),
    onSuccess: refreshCart,
    onError: handleMutationError,
  })

  const updateMutation = useMutation({
    mutationFn: ({ itemId, quantity }) => updateCartItem(itemId, quantity),
    onSuccess: refreshCart,
    onError: handleMutationError,
  })

  const removeMutation = useMutation({
    mutationFn: removeCartItem,
    onSuccess: refreshCart,
    onError: handleMutationError,
  })

  return {
    cart: cartQuery.data,
    isLoading: cartQuery.isPending,
    isError: cartQuery.isError,
    error: cartQuery.error,
    isFetching: cartQuery.isFetching,
    retryCart: cartQuery.refetch,
    addItem: (productId, quantity = 1) => addMutation.mutateAsync({ productId, quantity }),
    updateItem: (itemId, quantity) => updateMutation.mutateAsync({ itemId, quantity }),
    removeItem: (itemId) => removeMutation.mutateAsync(itemId),
    refreshCart,
  }
}

export default useCart
