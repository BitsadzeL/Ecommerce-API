package com.example.ecommerce.controller;

import com.example.ecommerce.dto.*;
import com.example.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductListItemResponse>> browseProducts() {
        return ResponseEntity.ok(productService.browseProducts());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDetailResponse> getProductDetail(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductDetails(id));
    }

    @GetMapping("/seller/products")
    public ResponseEntity<List<ProductResponse>> getOwnProducts(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(productService.getOwnProducts(userId));
    }

    @PostMapping("/products")
    public ResponseEntity<ProductResponse> createProduct(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateProductRequest request) {
        ProductResponse response = productService.createProduct(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(userId, id, request));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        productService.deleteProduct(userId, id);
        return ResponseEntity.noContent().build();
    }
}