package com.example.ecommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        Long id,
        Long sellerId,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        Boolean active,
        Instant createdAt
) {}
