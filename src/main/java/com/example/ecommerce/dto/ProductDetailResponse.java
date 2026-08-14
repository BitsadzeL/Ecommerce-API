package com.example.ecommerce.dto;

import java.math.BigDecimal;

public record ProductDetailResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Boolean inStock,
        String sellerDisplayName
) {}
