package com.primecart.exception;

public class DownstreamAuthenticationException extends RuntimeException {

    public DownstreamAuthenticationException(String message) {
        super(message);
    }
}