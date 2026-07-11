package com.primecart.exception;

public class OrderAlreadyExistsException extends RuntimeException {

    public OrderAlreadyExistsException(String message) {
        super(message);
    }
}