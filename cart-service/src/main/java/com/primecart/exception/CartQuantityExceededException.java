package com.primecart.exception;

public class CartQuantityExceededException extends RuntimeException {

    public CartQuantityExceededException(String message) {
        super(message);
    }
}