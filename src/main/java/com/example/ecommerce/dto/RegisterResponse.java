package com.example.ecommerce.dto;

import java.time.Instant;

public record RegisterResponse(
        Long id,
        String email,
        String phoneNumber,
        Instant createdAt
){}
