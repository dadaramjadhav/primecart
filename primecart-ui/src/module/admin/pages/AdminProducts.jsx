import { Button } from "@/shared/ui/button"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/shared/ui/table"
import { useState } from "react"
import useAdminProducts from "../hooks/useAdminProducts"
import { Link, useNavigate } from "react-router-dom"
import { buttonVariants } from "@/shared/ui/button"
import useDeleteAdminProduct from "../hooks/useDeleteAdminProduct"
import { showError, showSuccess } from "@/shared/utils/notifications"
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/shared/ui/alert-dialog"

function AdminProducts() {
  const [currentPage, setCurrentPage] = useState(0)
  const pageSize = 10
  const { mutateAsync: deleteProduct, isPending: isDeleting } = useDeleteAdminProduct()
  const { data, isPending, isError, error, isFetching, isPlaceholderData } = useAdminProducts(currentPage, pageSize)

  const products = data?.content ?? []
  const totalPages = data?.totalPages ?? 0
  const navigate = useNavigate()

  const openEditProduct = (productId) => {
    navigate(`/admin/products/${productId}/edit`)
  }

  if (isPending) {
    return <div>Loading products...</div>
  }
  async function handleDelete(productId) {
    try {
      await deleteProduct(productId)
      showSuccess("Product deleted successfully.")
    } catch (error) {
      showError(error, "Unable to delete product.")
    }
  }
  if (isError) {
    return <div className="text-destructive">{error?.message ?? "Unable to load products."}</div>
  }
  return (
    <div>
      <div className="mb-8 flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Product Management</h1>

          <p className="mt-2 text-muted-foreground">View, create, edit, and delete PrimeCart products.</p>
        </div>

        <Link to="/admin/products/new" className={buttonVariants()}>
          Add Product
        </Link>
      </div>

      <div className="rounded-xl border bg-card">
        {/* product table */}
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>ID</TableHead>
              <TableHead>Name</TableHead>
              <TableHead>Price</TableHead>
              <TableHead>Stock</TableHead>
              <TableHead className="text-right">Actions</TableHead>
            </TableRow>
          </TableHeader>

          <TableBody>
            {products.map((product) => (
              <TableRow
                key={product.id}
                className="cursor-pointer"
                tabIndex={0}
                onClick={() => openEditProduct(product.id)}
                onKeyDown={(event) => {
                  if (event.target === event.currentTarget && (event.key === "Enter" || event.key === " ")) {
                    event.preventDefault()
                    openEditProduct(product.id)
                  }
                }}>
                <TableCell>{product.id}</TableCell>

                <TableCell className="font-medium">{product.name}</TableCell>

                <TableCell>₹{product.price}</TableCell>

                <TableCell>{product.stock}</TableCell>

                <TableCell
                  onClick={(event) => event.stopPropagation()}
                  onKeyDown={(event) => event.stopPropagation()}>
                  <div className="flex justify-end gap-2">
                    <Link
                      to={`/admin/products/${product.id}/edit`}
                      className={buttonVariants({
                        variant: "outline",
                        size: "sm",
                      })}>
                      Edit
                    </Link>

                    <AlertDialog>
                      <AlertDialogTrigger
                        render={
                          <Button
                            variant="destructive"
                            size="sm"
                            disabled={isDeleting}
                          />
                        }>
                        Delete
                      </AlertDialogTrigger>

                      <AlertDialogContent>
                        <AlertDialogHeader>
                          <AlertDialogTitle>Delete {product.name}?</AlertDialogTitle>

                          <AlertDialogDescription>
                            This action cannot be undone. The product will be permanently removed from PrimeCart.
                          </AlertDialogDescription>
                        </AlertDialogHeader>

                        <AlertDialogFooter>
                          <AlertDialogCancel disabled={isDeleting}>Cancel</AlertDialogCancel>

                          <AlertDialogAction
                            variant="destructive"
                            disabled={isDeleting}
                            onClick={() => handleDelete(product.id)}>
                            {isDeleting ? "Deleting..." : "Delete Product"}
                          </AlertDialogAction>
                        </AlertDialogFooter>
                      </AlertDialogContent>
                    </AlertDialog>
                  </div>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>
      {/* pagination */}
      {totalPages > 1 && (
        <div className="mt-4 flex items-center justify-between">
          <Button
            variant="outline"
            disabled={currentPage === 0 || isPlaceholderData}
            onClick={() => setCurrentPage((page) => page - 1)}>
            Previous
          </Button>

          <div className="text-sm text-muted-foreground">
            Page {currentPage + 1} of {totalPages}
            {isFetching && " — Loading..."}
          </div>

          <Button
            variant="outline"
            disabled={currentPage >= totalPages - 1 || isPlaceholderData}
            onClick={() => setCurrentPage((page) => page + 1)}>
            Next
          </Button>
        </div>
      )}
    </div>
  )
}

export default AdminProducts
