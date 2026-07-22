package com.primecart.repository;

import com.primecart.entity.Cart;
import com.primecart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCart(Cart cart);

    void deleteByCart(Cart cart);

    Optional<CartItem> findByCartAndProductId(Cart cart, Long productId);
}