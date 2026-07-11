package com.primecart.mapper;

import com.primecart.dto.response.CartItemResponse;
import com.primecart.entity.CartItem;

public class CartMapper {

    private CartMapper() {
    }

    public static CartItemResponse toResponse(CartItem item) {

        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .build();
    }

}