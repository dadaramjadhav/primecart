import { useForm } from "react-hook-form"
import { Link } from "react-router-dom"

import { Button, buttonVariants } from "@/shared/ui/button"
import { Input } from "@/shared/ui/input"
import { Label } from "@/shared/ui/label"
import { Textarea } from "@/shared/ui/textarea"
import useAdminProductOptions from "../hooks/useAdminProductOptions"
import { isSafeHttpUrl } from "@/shared/utils/urlValidation"

const emptyProduct = {
  name: "",
  description: "",
  price: "",
  imageUrl: "https://picsum.photos/400?random=28",
  sku: "",
  stock: 0,
  active: true,
  categoryId: "",
  brandId: "",
}

function ProductForm({ initialValues = emptyProduct, onSubmit, submitLabel, isSaving = false }) {
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm({
    values: initialValues,
  })
  const { data: options, isPending: areOptionsLoading, isError: areOptionsUnavailable } = useAdminProductOptions()
  const categories = options?.categories ?? []
  const brands = options?.brands ?? []
  const submitting = isSubmitting || isSaving

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="max-w-2xl space-y-6 rounded-xl border bg-card p-6" noValidate>
      <div className="space-y-2">
        <Label htmlFor="name">Product name</Label>

        <Input
          id="name"
          placeholder="Enter product name"
          aria-invalid={Boolean(errors.name)}
          {...register("name", {
            required: "Product name is required.",
            minLength: {
              value: 3,
              message: "Product name must contain at least 3 characters.",
            },
          })}
        />

        {errors.name && <p className="text-sm text-destructive">{errors.name.message}</p>}
      </div>
      <div className="space-y-2">
        <Label htmlFor="sku">SKU</Label>

        <Input
          id="sku"
          placeholder="Example: LAPTOP-001"
          aria-invalid={Boolean(errors.sku)}
          {...register("sku", {
            required: "SKU is required.",
            minLength: {
              value: 3,
              message: "SKU must contain at least 3 characters.",
            },
          })}
        />

        {errors.sku && <p className="text-sm text-destructive">{errors.sku.message}</p>}
      </div>
      <div className="space-y-2">
        <Label htmlFor="description">Description</Label>

        <Textarea
          id="description"
          placeholder="Enter product description"
          rows={5}
          aria-invalid={Boolean(errors.description)}
          {...register("description", {
            required: "Product description is required.",
            minLength: {
              value: 10,
              message: "Description must contain at least 10 characters.",
            },
            maxLength: {
              value: 500,
              message: "Description cannot exceed 500 characters.",
            },
          })}
        />

        {errors.description && <p className="text-sm text-destructive">{errors.description.message}</p>}
      </div>
      <div className="space-y-2">
        <Label htmlFor="imageUrl">Image URL</Label>

        <Input
          id="imageUrl"
          type="url"
          placeholder="https://example.com/product-image.jpg"
          aria-invalid={Boolean(errors.imageUrl)}
          {...register("imageUrl", {
            required: "Product image URL is required.",

            validate: (value) => isSafeHttpUrl(value) || "Enter a valid HTTP or HTTPS image URL.",
          })}
        />

        {errors.imageUrl && <p className="text-sm text-destructive">{errors.imageUrl.message}</p>}
      </div>
      <div className="grid gap-6 sm:grid-cols-2">
        <div className="space-y-2">
          <Label htmlFor="price">Price</Label>

          <Input
            id="price"
            type="number"
            min="0.01"
            step="0.01"
            placeholder="Enter product price"
            aria-invalid={Boolean(errors.price)}
            {...register("price", {
              required: "Product price is required.",
              valueAsNumber: true,
              min: {
                value: 0.01,
                message: "Product price must be greater than zero.",
              },
            })}
          />

          {errors.price && <p className="text-sm text-destructive">{errors.price.message}</p>}
        </div>

        <div className="space-y-2">
          <Label htmlFor="stock">Stock quantity</Label>

          <Input
            id="stock"
            type="number"
            min="0"
            step="1"
            placeholder="Enter available stock"
            aria-invalid={Boolean(errors.stock)}
            {...register("stock", {
              required: "Stock quantity is required.",
              valueAsNumber: true,
              min: {
                value: 0,
                message: "Stock quantity cannot be negative.",
              },
              validate: (value) => Number.isInteger(value) || "Stock quantity must be a whole number.",
            })}
          />

          {errors.stock && <p className="text-sm text-destructive">{errors.stock.message}</p>}
        </div>
      </div>
      <div className="grid gap-6 sm:grid-cols-2">
        <div className="space-y-2">
          <Label htmlFor="categoryId">Category</Label>

          <select
            id="categoryId"
            disabled={areOptionsLoading || areOptionsUnavailable}
            aria-invalid={Boolean(errors.categoryId)}
            className="h-8 w-full rounded-lg border border-input bg-transparent px-2.5 py-1 text-sm outline-none focus-visible:border-ring focus-visible:ring-3 focus-visible:ring-ring/50 disabled:cursor-not-allowed disabled:opacity-50 aria-invalid:border-destructive"
            {...register("categoryId", {
              required: "Category is required.",
              valueAsNumber: true,
              validate: (value) => (Number.isInteger(value) && value > 0) || "Select a valid category.",
            })}>
            <option value="">{areOptionsLoading ? "Loading categories..." : "Select a category"}</option>

            {categories.map((category) => (
              <option key={category.id} value={category.id}>
                {category.name}
                {category.active === false ? " (Inactive)" : ""}
              </option>
            ))}
          </select>

          {errors.categoryId && <p className="text-sm text-destructive">{errors.categoryId.message}</p>}
        </div>
        <div className="space-y-2">
          <Label htmlFor="brandId">Brand</Label>

          <select
            id="brandId"
            disabled={areOptionsLoading || areOptionsUnavailable}
            aria-invalid={Boolean(errors.brandId)}
            className="h-8 w-full rounded-lg border border-input bg-transparent px-2.5 py-1 text-sm outline-none focus-visible:border-ring focus-visible:ring-3 focus-visible:ring-ring/50 disabled:cursor-not-allowed disabled:opacity-50 aria-invalid:border-destructive"
            {...register("brandId", {
              required: "Brand is required.",
              valueAsNumber: true,
              validate: (value) => (Number.isInteger(value) && value > 0) || "Select a valid brand.",
            })}>
            <option value="">{areOptionsLoading ? "Loading brands..." : "Select a brand"}</option>

            {brands.map((brand) => (
              <option key={brand.id} value={brand.id}>
                {brand.name}
                {brand.active === false ? " (Inactive)" : ""}
              </option>
            ))}
          </select>

          {errors.brandId && <p className="text-sm text-destructive">{errors.brandId.message}</p>}
        </div>
      </div>
      {areOptionsUnavailable && (
        <p className="text-sm text-destructive">
          Unable to load categories and brands. Refresh the page and try again.
        </p>
      )}

      <div className="flex items-center gap-3">
        <input id="active" type="checkbox" className="h-4 w-4" {...register("active")} />

        <Label htmlFor="active">Active product</Label>
      </div>
      <div className="flex items-center gap-3">
        <Button type="submit" disabled={submitting || areOptionsLoading || areOptionsUnavailable}>
          {submitting ? "Saving..." : submitLabel}
        </Button>
        <Link
          to="/admin/products"
          className={buttonVariants({
            variant: "outline",
          })}>
          Cancel
        </Link>
      </div>
    </form>
  )
}

export default ProductForm
