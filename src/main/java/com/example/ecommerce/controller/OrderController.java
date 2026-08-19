package com.example.ecommerce.controller;

import com.example.ecommerce.dto.BuyNowRequest;
import com.example.ecommerce.dto.OrderResponse;
import com.example.ecommerce.dto.OrderSummaryResponse;
import com.example.ecommerce.dto.PayRequest;
import com.example.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(@AuthenticationPrincipal Long customerId) {
        OrderResponse response = orderService.checkout(customerId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/buy-now")
    public ResponseEntity<OrderResponse> buyNow(
            @AuthenticationPrincipal Long customerId,
            @Valid @RequestBody BuyNowRequest request) {
        OrderResponse response = orderService.buyNow(customerId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<OrderResponse> pay(
            @AuthenticationPrincipal Long customerId,
            @PathVariable Long id,
            @Valid @RequestBody PayRequest request) {
        OrderResponse response = orderService.pay(customerId, id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<OrderSummaryResponse>> getOrders(
            @AuthenticationPrincipal Long customerId) {
        List<OrderSummaryResponse> response = orderService.getOrders(customerId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(
            @AuthenticationPrincipal Long customerId,
            @PathVariable Long id) {
        OrderResponse response = orderService.getOrder(customerId, id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
