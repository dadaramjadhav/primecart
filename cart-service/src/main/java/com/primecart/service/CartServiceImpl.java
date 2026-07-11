package com.primecart.service;

import com.primecart.client.ProductClient;
import com.primecart.dto.request.AddCartItemRequest;
import com.primecart.dto.request.UpdateCartItemRequest;
import com.primecart.dto.response.CartItemResponse;
import com.primecart.dto.response.CartResponse;
import com.primecart.dto.response.ProductResponse;
import com.primecart.entity.Cart;
import com.primecart.entity.CartItem;
import com.primecart.exception.CartItemNotFoundException;
import com.primecart.exception.CartNotFoundException;
import com.primecart.repository.CartItemRepository;
import com.primecart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductClient productClient;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(String userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElse(null);

        if (cart == null) {
            return CartResponse.builder()
                    .cartId(null)
                    .userId(userId)
                    .items(List.of())
                    .totalAmount(BigDecimal.ZERO)
                    .build();
        }

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        return buildCartResponse(cart, cartItems);
    }

    @Override
    public CartResponse addItem(String userId,
                                AddCartItemRequest request) {

        // Get existing cart or create a new one
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> createCart(userId));

        // Validate product via Product Service
        ProductResponse product = getProduct(request.getProductId());

        // Check if the product already exists in the cart
        CartItem cartItem = cartItemRepository
                .findByCartAndProductId(cart, request.getProductId())
                .orElse(null);

        if (cartItem != null) {

            // Increase quantity
            int newQuantity = cartItem.getQuantity() + request.getQuantity();

            cartItem.setQuantity(newQuantity);
            cartItem.setSubtotal(
                    cartItem.getPrice()
                            .multiply(BigDecimal.valueOf(newQuantity))
            );

        } else {

            // Create a new cart item
            cartItem = CartItem.builder()
                    .cart(cart)
                    .productId(product.getId())
                    .productName(product.getName())
                    .price(product.getPrice())
                    .quantity(request.getQuantity())
                    .subtotal(
                            product.getPrice()
                                    .multiply(BigDecimal.valueOf(request.getQuantity()))
                    )
                    .build();
        }

        cartItemRepository.save(cartItem);

        List<CartItem> items = cartItemRepository.findByCart(cart);

        return buildCartResponse(cart, items);
    }

    @Override
    public CartResponse updateItem(String userId,
                                   Long cartItemId,
                                   UpdateCartItemRequest request) {

        Cart cart = getCartByUser(userId);

        CartItem cartItem = getCartItem(cartItemId);

        validateOwnership(cart, cartItem);

        cartItem.setQuantity(request.getQuantity());

        cartItem.setSubtotal(
                cartItem.getPrice()
                        .multiply(BigDecimal.valueOf(request.getQuantity()))
        );

        cartItemRepository.save(cartItem);

        List<CartItem> items = cartItemRepository.findByCart(cart);

        return buildCartResponse(cart, items);
    }

    @Override
    public void removeItem(String userId,
                           Long cartItemId) {

        Cart cart = getCartByUser(userId);

        CartItem cartItem = getCartItem(cartItemId);

        validateOwnership(cart, cartItem);

        cartItemRepository.delete(cartItem);
    }

    @Override
    public void clearCart(String userId) {

        Cart cart = getCartByUser(userId);

        cartItemRepository.deleteByCart(cart);
    }

    // =====================================================
    // Helper Methods
    // =====================================================

    private Cart createCart(String userId) {

        Cart cart = Cart.builder()
                .userId(userId)
                .build();

        return cartRepository.save(cart);
    }

    private CartResponse buildCartResponse(Cart cart,
                                           List<CartItem> cartItems) {

        List<CartItemResponse> items = new ArrayList<>();

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : cartItems) {

            items.add(toResponse(item));

            total = total.add(item.getSubtotal());
        }

        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUserId())
                .items(items)
                .totalAmount(total)
                .build();
    }

    private CartItemResponse toResponse(CartItem item) {

        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .build();
    }

    private Cart getCartByUser(String userId) {

        return cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new CartNotFoundException("Cart not found."));
    }

    private CartItem getCartItem(Long cartItemId) {

        return cartItemRepository.findById(cartItemId)
                .orElseThrow(() ->
                        new CartItemNotFoundException("Cart item not found."));
    }

    private void validateOwnership(Cart cart,
                                   CartItem cartItem) {

        if (!cart.getId().equals(cartItem.getCart().getId())) {

            throw new RuntimeException(
                    "Cart item does not belong to authenticated user.");
        }
    }

    private ProductResponse getProduct(Long productId) {

        ProductResponse product =
                productClient.getProductById(productId);

        if (product == null || Boolean.FALSE.equals(product.getActive())) {

            throw new RuntimeException("Product is not available.");
        }

        return product;
    }
}