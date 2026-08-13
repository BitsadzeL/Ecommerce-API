package com.example.ecommerce.dto;

import java.time.Instant;

public record SellerProfileResponse(
        Long id,
        Long userId,
        String displayName,
        Instant createdAt
) {}
