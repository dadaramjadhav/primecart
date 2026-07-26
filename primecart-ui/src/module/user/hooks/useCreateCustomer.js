import { useMutation, useQueryClient } from "@tanstack/react-query"
import { createCustomerProfile } from "../services/customerService"

export default function useCreateCustomer() {
  const queryClient = useQueryClient()

  const mutation = useMutation({
    mutationFn: createCustomerProfile,
    onSuccess: (customer) => {
      queryClient.setQueryData(["customers", "profile"], customer)
    },
  })

  return {
    createCustomer: mutation.mutateAsync,
    isCreating: mutation.isPending,
  }
}
