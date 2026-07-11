package com.primecart.service;

import com.primecart.dto.request.AddCartItemRequest;
import com.primecart.dto.request.UpdateCartItemRequest;
import com.primecart.dto.response.CartResponse;

public interface CartService {

    CartResponse getCart(String userId);

    CartResponse addItem(String userId, AddCartItemRequest request);

    CartResponse updateItem(String userId,
                            Long cartItemId,
                            UpdateCartItemRequest request);

    void removeItem(String userId, Long cartItemId);

    void clearCart(String userId);
}