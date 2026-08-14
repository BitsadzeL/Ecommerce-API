package com.example.ecommerce.handler;

import com.example.ecommerce.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import static org.springframework.http.HttpStatus.NOT_FOUND;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : fieldErrors) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return new ResponseEntity<>(errors, BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<EntityErrorResponse> emailAlreadyExists(EmailAlreadyExistsException e) {
        EntityErrorResponse error = new EntityErrorResponse(
                e.getMessage(),
                CONFLICT,
                System.currentTimeMillis());
        return new ResponseEntity<>(error, CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<EntityErrorResponse> userNotFound(UserNotFoundException e) {
        EntityErrorResponse error = new EntityErrorResponse(
                e.getMessage(),
                NOT_FOUND,
                System.currentTimeMillis());
        return new ResponseEntity<>(error, NOT_FOUND);
    }

    @ExceptionHandler(SellerProfileAlreadyExistsException.class)
    public ResponseEntity<EntityErrorResponse> sellerProfileAlreadyExists(SellerProfileAlreadyExistsException e) {
        EntityErrorResponse error = new EntityErrorResponse(
                e.getMessage(),
                CONFLICT,
                System.currentTimeMillis());
        return new ResponseEntity<>(error, CONFLICT);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<EntityErrorResponse> productNotFound(ProductNotFoundException e) {
        EntityErrorResponse error = new EntityErrorResponse(
                e.getMessage(),
                NOT_FOUND,
                System.currentTimeMillis());
        return new ResponseEntity<>(error, NOT_FOUND);
    }

    @ExceptionHandler(NotASellerException.class)
    public ResponseEntity<EntityErrorResponse> notASeller(NotASellerException e) {
        EntityErrorResponse error = new EntityErrorResponse(
                e.getMessage(),
                FORBIDDEN,
                System.currentTimeMillis());
        return new ResponseEntity<>(error, FORBIDDEN);
    }

    @ExceptionHandler(ProductOwnershipException.class)
    public ResponseEntity<EntityErrorResponse> productOwnership(ProductOwnershipException e) {
        EntityErrorResponse error = new EntityErrorResponse(
                e.getMessage(),
                FORBIDDEN,
                System.currentTimeMillis());
        return new ResponseEntity<>(error, FORBIDDEN);
    }


    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<EntityErrorResponse> insufficientStock(OutOfStockException e) {
        EntityErrorResponse error = new EntityErrorResponse(e.getMessage(), BAD_REQUEST, System.currentTimeMillis());
        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<EntityErrorResponse> cartItemNotFound(CartItemNotFoundException e) {
        EntityErrorResponse error = new EntityErrorResponse(e.getMessage(), NOT_FOUND, System.currentTimeMillis());
        return new ResponseEntity<>(error, NOT_FOUND);
    }

    @ExceptionHandler(CartItemOwnershipException.class)
    public ResponseEntity<EntityErrorResponse> cartItemOwnership(CartItemOwnershipException e) {
        EntityErrorResponse error = new EntityErrorResponse(e.getMessage(), FORBIDDEN, System.currentTimeMillis());
        return new ResponseEntity<>(error, FORBIDDEN);
    }
}