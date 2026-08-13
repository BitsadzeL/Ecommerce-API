package com.example.ecommerce.exception;

public class SellerProfileAlreadyExistsException extends RuntimeException {
    public SellerProfileAlreadyExistsException(String message) {
        super(message);
    }
}