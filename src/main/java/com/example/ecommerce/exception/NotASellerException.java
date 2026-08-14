package com.example.ecommerce.exception;

public class NotASellerException extends RuntimeException {
    public NotASellerException(String message) {
        super(message);
    }
}
