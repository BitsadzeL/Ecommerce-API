package com.example.ecommerce.dto;

import java.math.BigDecimal;

public record ProductListItemResponse(
        Long id,
        String name,
        BigDecimal price
) {}
