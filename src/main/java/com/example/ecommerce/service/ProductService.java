package com.example.ecommerce.service;

import com.example.ecommerce.dto.*;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.SellerProfile;
import com.example.ecommerce.exception.NotASellerException;
import com.example.ecommerce.exception.ProductNotFoundException;
import com.example.ecommerce.exception.ProductOwnershipException;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.SellerProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final SellerProfileRepository sellerProfileRepository;

    public ProductService(ProductRepository productRepository,
                          SellerProfileRepository sellerProfileRepository) {
        this.productRepository = productRepository;
        this.sellerProfileRepository = sellerProfileRepository;
    }



    public List<ProductListItemResponse> browseProducts() {
        return productRepository.findByActiveTrue().stream()
                .map(p -> new ProductListItemResponse(p.getId(), p.getName(), p.getPrice()))
                .toList();
    }

    public ProductDetailResponse getProductDetails(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));

        if(!product.getActive()){
            throw new ProductNotFoundException("Product not found: " + productId);
        }

        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity() > 0,
                product.getSellerProfile().getDisplayName()
        );
    }



    public List<ProductResponse> getOwnProducts(Long userId) {
        SellerProfile sellerProfile = sellerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotASellerException("User " + userId + " is not a seller"));

        return productRepository.findBySellerProfileId(sellerProfile.getId()).stream()
                .map(this::toProductResponse)
                .toList();
    }

    public ProductResponse createProduct(Long userId, CreateProductRequest request) {
        SellerProfile sellerProfile = sellerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotASellerException("User " + userId + " is not a seller"));

        Product product = new Product();
        product.setSellerProfile(sellerProfile);
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setActive(true);

        Product saved = productRepository.save(product);
        return toProductResponse(saved);
    }

    public ProductResponse updateProduct(Long userId, Long productId, UpdateProductRequest request) {
        Product product = getOwnedProductOrThrow(userId, productId);

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());

        Product saved = productRepository.save(product);
        return toProductResponse(saved);
    }

    public void deleteProduct(Long userId, Long productId) {
        Product product = getOwnedProductOrThrow(userId, productId);
        product.setActive(false);
        productRepository.save(product);
    }



    private Product getOwnedProductOrThrow(Long userId, Long productId) {
        SellerProfile sellerProfile = sellerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotASellerException("User " + userId + " is not a seller"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));

        if (!product.getSellerProfile().getId().equals(sellerProfile.getId())) {
            throw new ProductOwnershipException(
                    "User " + userId + " does not own product " + productId);
        }

        return product;
    }

    private ProductResponse toProductResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getSellerProfile().getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStockQuantity(),
                p.getActive(),
                p.getCreatedAt()
        );
    }
}