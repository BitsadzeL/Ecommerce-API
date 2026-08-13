package com.example.ecommerce.controller;

import com.example.ecommerce.dto.BecomeSellerRequest;
import com.example.ecommerce.dto.SellerProfileResponse;
import com.example.ecommerce.service.SellerProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller")
public class SellerController {

    private final SellerProfileService sellerProfileService;

    public SellerController(SellerProfileService sellerProfileService) {
        this.sellerProfileService = sellerProfileService;
    }

    @PostMapping("/profile")
    public ResponseEntity<SellerProfileResponse> becomeSeller(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody BecomeSellerRequest request) {
        SellerProfileResponse response = sellerProfileService.becomeSeller(userId,request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}