import { useEffect, useState } from "react"
import { useParams } from "react-router-dom"
import { addToCart } from "../services/cartService"
import { getProduct } from "../services/productService"

function ProductDetails() {
  const { id } = useParams()
  const [quantity, setQuantity] = useState(1)

  const [product, setProduct] = useState(null)
  async function handleAddToCart() {
    await addToCart(product.id, quantity)

    alert("Added to cart")
  }
  useEffect(() => {
    async function loadProduct() {
      const data = await getProduct(id)
      setProduct(data)
    }
    loadProduct()
  }, [])

  if (!product) {
    return <h2>Loading...</h2>
  }

  return (
    <div>
      <h1>{product.name}</h1>

      <img src={product.imageUrl} alt={product.name} width={300} />

      <h2>₹ {product.price}</h2>

      <p>{product.description}</p>

      <div className="flex gap-3">
        <button onClick={() => setQuantity((q) => Math.max(1, q - 1))}>-</button>

        <span>{quantity}</span>

        <button onClick={() => setQuantity((q) => q + 1)}>+</button>
      </div>
      <button onClick={handleAddToCart} className="bg-blue-600 text-white px-5 py-2 rounded">
        Add to Cart
      </button>
    </div>
  )
}

export default ProductDetails
