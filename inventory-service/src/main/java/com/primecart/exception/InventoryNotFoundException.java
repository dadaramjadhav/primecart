package com.primecart.exception;

public class InventoryNotFoundException
        extends RuntimeException {

    public InventoryNotFoundException(String message) {

        super(message);

    }

}