package com.primecart.controller;


import com.primecart.config.CurrentUser;
import com.primecart.dto.request.AddCartItemRequest;
import com.primecart.dto.request.UpdateCartItemRequest;
import com.primecart.dto.response.CartResponse;
import com.primecart.service.CartService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {


    private final CartService cartService;



    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            @CurrentUser String userId) {


        log.info("Authenticated userId: {}", userId);


        return ResponseEntity.ok(
                cartService.getCart(userId)
        );
    }



    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(
            @CurrentUser String userId,
            @Valid @RequestBody AddCartItemRequest request) {


        log.info("Authenticated userId: {}", userId);


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        cartService.addItem(userId, request)
                );
    }



    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateItem(
            @CurrentUser String userId,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request) {


        log.info("Authenticated userId: {}", userId);


        return ResponseEntity.ok(
                cartService.updateItem(
                        userId,
                        cartItemId,
                        request
                )
        );
    }



    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> removeItem(
            @CurrentUser String userId,
            @PathVariable Long cartItemId) {


        cartService.removeItem(
                userId,
                cartItemId
        );


        return ResponseEntity.noContent()
                .build();
    }



    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            @CurrentUser String userId) {


        cartService.clearCart(userId);


        return ResponseEntity.noContent()
                .build();
    }
}