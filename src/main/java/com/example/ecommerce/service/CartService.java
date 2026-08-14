package com.example.ecommerce.service;

import com.example.ecommerce.dto.AddCartItemRequest;
import com.example.ecommerce.dto.CartItemResponse;
import com.example.ecommerce.dto.CartResponse;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.*;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public CartResponse addOrUpdateItem(Long userId, AddCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product not found: " + request.productId()));

        if (request.quantity() > product.getStockQuantity()) {
            throw new OutOfStockException(
                    "Only " + product.getStockQuantity() + " of \"" + product.getName()
                            + "\" available, requested " + request.quantity());
        }

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    return newItem;
                });

        item.setQuantity(request.quantity());
        cartItemRepository.save(item);

        cart.setUpdatedAt(java.time.Instant.now());
        cartRepository.save(cart);

        return getCart(userId);
    }

    public CartResponse getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);

        List<CartItemResponse> items = cartItemRepository.findByCartId(cart.getId()).stream()
                .map(this::toCartItemResponse)
                .toList();

        return new CartResponse(cart.getId(), items);
    }

    public void removeItem(Long userId, Long cartItemId) {
        Cart cart = getOrCreateCart(userId);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(
                        "Cart item not found: " + cartItemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new CartItemOwnershipException(
                    "Cart item " + cartItemId + " does not belong to user " + userId);
        }

        cartItemRepository.delete(item);
    }


    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByCustomerId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
                    Cart newCart = new Cart();
                    newCart.setCustomer(user);
                    return cartRepository.save(newCart);
                });
    }

    private CartItemResponse toCartItemResponse(CartItem item) {
        Product product = item.getProduct();
        return new CartItemResponse(
                item.getId(),
                product.getId(),
                product.getName(),
                product.getPrice(),
                item.getQuantity()
        );
    }
}