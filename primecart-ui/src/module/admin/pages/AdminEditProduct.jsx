import { useNavigate, useParams } from "react-router-dom"

import { showError, showSuccess } from "@/shared/utils/notifications"
import ProductForm from "../components/ProductForm"
import useAdminProduct from "../hooks/useAdminProduct"
import useUpdateAdminProduct from "../hooks/useUpdateAdminProduct"

function AdminEditProduct() {
  const { productId } = useParams()
  const navigate = useNavigate()

  const { data: product, isPending: isLoading, isError, error } = useAdminProduct(productId)

  const { mutateAsync: updateProduct, isPending: isUpdating } = useUpdateAdminProduct()

  if (isLoading) {
    return <div>Loading product...</div>
  }

  if (isError) {
    return <div className="text-destructive">{error?.message ?? "Unable to load product."}</div>
  }

  const initialValues = {
    name: product.name ?? "",
    description: product.description ?? "",
    price: product.price ?? "",
    imageUrl: product.imageUrl ?? "",
    sku: product.sku ?? "",
    stock: product.stock ?? 0,
    active: product.active ?? true,
    categoryId: product.categoryId ?? product.category?.id ?? "",
    brandId: product.brandId ?? product.brand?.id ?? "",
  }

  async function handleUpdate(productData) {
    try {
      await updateProduct({
        productId,
        productData,
      })

      showSuccess("Product updated successfully.")

      navigate("/admin/products")
    } catch (updateError) {
      showError(updateError, "Unable to update product.")
    }
  }

  return (
    <div>
      <div className="mb-8">
        <h1 className="text-3xl font-bold">Edit Product</h1>

        <p className="mt-2 text-muted-foreground">Update {product.name}.</p>
      </div>

      <ProductForm
        initialValues={initialValues}
        onSubmit={handleUpdate}
        submitLabel="Update Product"
        isSaving={isUpdating}
      />
    </div>
  )
}

export default AdminEditProduct
