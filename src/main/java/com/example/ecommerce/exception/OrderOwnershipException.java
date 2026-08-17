package com.example.ecommerce.exception;

public class OrderOwnershipException extends RuntimeException {
    public OrderOwnershipException(String message) {
        super(message);
    }
}
