package com.example.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BuyNowRequest(
        @NotNull Long productId,
        @NotNull @Positive Integer quantity
) {}
