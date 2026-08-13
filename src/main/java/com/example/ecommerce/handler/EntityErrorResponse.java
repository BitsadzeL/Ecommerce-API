package com.example.ecommerce.handler;

import org.springframework.http.HttpStatus;

public record EntityErrorResponse(String message,
                                  HttpStatus httpStatus,
                                  long timestamp) {
}
