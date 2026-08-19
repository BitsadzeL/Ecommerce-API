package com.example.ecommerce.controller;

import com.example.ecommerce.dto.BecomeSellerRequest;
import com.example.ecommerce.dto.SellerOrderResponse;
import com.example.ecommerce.dto.SellerProfileResponse;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.service.SellerProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seller")
public class SellerController {

    private final SellerProfileService sellerProfileService;
    private final OrderService orderService;

    public SellerController(SellerProfileService sellerProfileService, OrderService orderService) {
        this.sellerProfileService = sellerProfileService;
        this.orderService = orderService;
    }

    @PostMapping("/profile")
    public ResponseEntity<SellerProfileResponse> becomeSeller(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody BecomeSellerRequest request) {
        SellerProfileResponse response = sellerProfileService.becomeSeller(userId,request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<SellerOrderResponse>> getSellerOrders(
            @AuthenticationPrincipal Long userId) {
        List<SellerOrderResponse> response = orderService.getSellerOrders(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}