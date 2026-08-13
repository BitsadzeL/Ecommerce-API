package com.example.ecommerce.handler;

import com.example.ecommerce.exception.EmailAlreadyExistsException;
import com.example.ecommerce.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}