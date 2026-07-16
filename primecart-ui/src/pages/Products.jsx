import { useEffect, useState } from "react"
import { getProducts } from "../services/productService"
import { Link } from "react-router-dom"

function Products() {
  const [products, setProducts] = useState([])

  useEffect(() => {
    const loadProducts = async () => {
      try {
        const data = await getProducts()
        setProducts(data.content)
      } catch (error) {
        console.error(error)
      }
    }

    loadProducts()
  }, [])
  return (
    <div className="mx-auto max-w-7xl">
      <h1 className="mb-8 text-3xl font-bold">Products</h1>

      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
        {products.map((product) => (
          <div key={product.id} className="rounded-lg border bg-white p-4 shadow">
            <Link to={`/products/${product.id}`}>
              <img src={product.imageUrl} alt={product.name} />
            </Link>

            <h2 className="text-xl font-semibold">{product.name}(id:{product.id})</h2>

            <p className="mt-2 text-gray-500">{product.description}</p>

            <p className="mt-3 text-lg font-bold text-blue-600">₹{product.price}</p>
          </div>
        ))}
      </div>
    </div>
  )
}

export default Products
