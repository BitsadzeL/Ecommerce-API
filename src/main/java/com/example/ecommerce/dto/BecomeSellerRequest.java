package com.example.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;

public record BecomeSellerRequest(
        @NotBlank String displayName
) {}