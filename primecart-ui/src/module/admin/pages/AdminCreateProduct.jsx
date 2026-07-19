import { useNavigate } from "react-router-dom"

import { showError, showSuccess } from "@/shared/utils/notifications"
import ProductForm from "../components/ProductForm"
import useCreateAdminProduct from "../hooks/useCreateAdminProduct"

function AdminCreateProduct() {
  const navigate = useNavigate()

  const { mutateAsync: createProduct, isPending } = useCreateAdminProduct()

  async function handleCreate(productData) {
    try {
      await createProduct(productData)

      showSuccess("Product created successfully.")

      navigate("/admin/products")
    } catch (error) {
      showError(error, "Unable to create product.")
    }
  }

  return (
    <div>
      <div className="mb-8">
        <h1 className="text-3xl font-bold">Add Product</h1>

        <p className="mt-2 text-muted-foreground">Create a new product for the PrimeCart catalogue.</p>
      </div>

      <ProductForm onSubmit={handleCreate} submitLabel="Create Product" isSaving={isPending} />
    </div>
  )
}

export default AdminCreateProduct
