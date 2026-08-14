package com.example.ecommerce.exception;

public class CartItemOwnershipException extends RuntimeException {
    public CartItemOwnershipException(String message) {
        super(message);
    }
}
