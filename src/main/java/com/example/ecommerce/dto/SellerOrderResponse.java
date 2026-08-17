package com.example.ecommerce.dto;

import com.example.ecommerce.enums.SellerOrderStatus;

import java.util.List;

public record SellerOrderResponse(
        Long id,
        Long sellerProfileId,
        String sellerDisplayName,
        SellerOrderStatus status,
        List<OrderItemResponse> items
) {}
