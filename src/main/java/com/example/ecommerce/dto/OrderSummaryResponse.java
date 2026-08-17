package com.example.ecommerce.dto;

import com.example.ecommerce.enums.OrderStatus;

import java.time.LocalDateTime;

public record OrderSummaryResponse(
        Long id,
        OrderStatus status,
        LocalDateTime createdAt,
        int sellerCount
) {}
