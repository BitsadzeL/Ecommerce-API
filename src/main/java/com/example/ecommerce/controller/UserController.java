package com.example.ecommerce.controller;

import com.example.ecommerce.dto.UpdatePhoneNumberRequest;
import com.example.ecommerce.dto.UserResponse;
import com.example.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updatePhoneNumber(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UpdatePhoneNumberRequest request) {
        return ResponseEntity.ok(userService.updatePhoneNumber(userId, request));
    }
}