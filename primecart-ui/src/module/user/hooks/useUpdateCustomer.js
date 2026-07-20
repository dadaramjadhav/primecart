import { useMutation, useQueryClient } from "@tanstack/react-query"

import { updateCustomerProfile } from "../services/customerService"

function useUpdateCustomer() {
  const queryClient = useQueryClient()

  const updateMutation = useMutation({
    mutationFn: updateCustomerProfile,

    onSuccess: (updatedCustomer) => {
      queryClient.setQueryData(["customer", "profile"], updatedCustomer)
    },
  })

  return {
    updateCustomer: updateMutation.mutateAsync,
    isUpdating: updateMutation.isPending,
  }
}

export default useUpdateCustomer
