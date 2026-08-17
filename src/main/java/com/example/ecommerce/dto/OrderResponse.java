package com.example.ecommerce.dto;

import com.example.ecommerce.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        OrderStatus status,
        LocalDateTime createdAt,
        List<SellerOrderResponse> sellerOrders
) {



}
