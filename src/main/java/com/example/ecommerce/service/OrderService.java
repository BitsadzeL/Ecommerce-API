package com.example.ecommerce.service;

import com.example.ecommerce.dto.*;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.enums.OrderStatus;
import com.example.ecommerce.enums.SellerOrderStatus;
import com.example.ecommerce.exception.*;
import com.example.ecommerce.repository.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final SellerOrderRepository sellerOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final EntityManager entityManager;

    @Transactional
    public OrderResponse checkout(Long customerId) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for customer " + customerId));

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

        if (cartItems.isEmpty()) {
            throw new EmptyCartException("Cannot checkout an empty cart");
        }

        List<OrderCreationLineItem> lineItems = cartItems.stream()
                .map(ci -> new OrderCreationLineItem(ci.getProduct().getId(), ci.getQuantity()))
                .toList();

        OrderResponse response = createOrder(customerId, lineItems);
        cartItemRepository.deleteAll(cartItems);
        return response;
    }

    @Transactional
    public OrderResponse buyNow(Long customerId, BuyNowRequest request) {
        List<OrderCreationLineItem> lineItems = List.of(
                new OrderCreationLineItem(request.productId(), request.quantity())
        );
        return createOrder(customerId, lineItems);
    }

    @Transactional
    public OrderResponse pay(Long customerId, Long orderId, PayRequest request) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

        if (!order.getCustomer().getId().equals(customerId)) {
            throw new OrderOwnershipException("Order does not belong to this customer");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException(
                    "Cannot pay for order in status " + order.getStatus());
        }

        boolean paymentSucceeded = !request.simulateFailure();

        if (paymentSucceeded) {
            order.setStatus(OrderStatus.PAID);

            List<SellerOrder> sellerOrders = sellerOrderRepository.findByOrderId(orderId);
            for (SellerOrder so : sellerOrders) {
                so.setStatus(SellerOrderStatus.CONFIRMED);
            }

        } else {
            order.setStatus(OrderStatus.PAYMENT_FAILED);

            List<OrderItem> items = orderItemRepository.findAllByOrderIdWithDetails(orderId);
            for (OrderItem item : items) {
                Product product = item.getProduct();
                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            }
        }

        return buildOrderResponse(order);
    }


    public List<OrderSummaryResponse> getOrders(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);

        return orders.stream()
                .map(order -> {
                    int sellerCount = sellerOrderRepository.findByOrderId(order.getId()).size();
                    return new OrderSummaryResponse(
                            order.getId(),
                            order.getStatus(),
                            order.getCreatedAt(),
                            sellerCount
                    );
                })
                .toList();
    }

    public OrderResponse getOrder(Long customerId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

        if (!order.getCustomer().getId().equals(customerId)) {
            throw new OrderOwnershipException("Order does not belong to this customer");
        }

        return buildOrderResponse(order);
    }

    public List<SellerOrderResponse> getSellerOrders(Long customerId) {
        SellerProfile sellerProfile = sellerProfileRepository.findByUserId(customerId)
                .orElseThrow(() -> new NotASellerException("User is not a seller: " + customerId));

        List<OrderItem> items = orderItemRepository.findAllBySellerProfileIdWithDetails(sellerProfile.getId());

        Map<SellerOrder, List<OrderItem>> bySellerOrder = items.stream()
                .collect(Collectors.groupingBy(
                        OrderItem::getSellerOrder,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        return bySellerOrder.entrySet().stream()
                .map(entry -> {
                    SellerOrder so = entry.getKey();
                    List<OrderItemResponse> itemResponses = entry.getValue().stream()
                            .map(oi -> new OrderItemResponse(
                                    oi.getProduct().getId(),
                                    oi.getProduct().getName(),
                                    oi.getQuantity(),
                                    oi.getPriceAtPurchase()
                            ))
                            .toList();

                    return new SellerOrderResponse(
                            so.getId(),
                            sellerProfile.getId(),
                            sellerProfile.getDisplayName(),
                            so.getStatus(),
                            itemResponses
                    );
                })
                .toList();
    }



    private OrderResponse createOrder(Long customerId, List<OrderCreationLineItem> lineItems) {

        Map<Long, Product> productsById = lineItems.stream()
                .map(li -> productRepository.findByIdWithLock(li.productId())
                        .orElseThrow(() -> new ProductNotFoundException(
                                "Product not found: " + li.productId())))
                .collect(Collectors.toMap(Product::getId, p -> p));

        for (OrderCreationLineItem li : lineItems) {
            Product product = productsById.get(li.productId());
            if (product.getStockQuantity() < li.quantity()) {
                throw new OutOfStockException(
                        "Insufficient stock for product " + product.getId());
            }
            product.setStockQuantity(product.getStockQuantity() - li.quantity());
            productRepository.save(product);
        }

        Order order = new Order();
        order.setCustomer(entityManager.getReference(User.class, customerId));
        order.setStatus(OrderStatus.PENDING);
        order = orderRepository.save(order);

        Map<Long, List<OrderCreationLineItem>> lineItemsBySeller = lineItems.stream()
                .collect(Collectors.groupingBy(
                        li -> productsById.get(li.productId()).getSellerProfile().getId()
                ));

        for (Map.Entry<Long, List<OrderCreationLineItem>> entry : lineItemsBySeller.entrySet()) {
            SellerProfile sellerProfile = productsById.values().stream()
                    .filter(p -> p.getSellerProfile().getId().equals(entry.getKey()))
                    .findFirst()
                    .map(Product::getSellerProfile)
                    .orElseThrow();

            SellerOrder sellerOrder = new SellerOrder();
            sellerOrder.setOrder(order);
            sellerOrder.setSellerProfile(sellerProfile);
            sellerOrder.setStatus(SellerOrderStatus.PENDING);
            sellerOrder = sellerOrderRepository.save(sellerOrder);

            for (OrderCreationLineItem li : entry.getValue()) {
                Product product = productsById.get(li.productId());

                OrderItem item = new OrderItem();
                item.setSellerOrder(sellerOrder);
                item.setProduct(product);
                item.setQuantity(li.quantity());
                item.setPriceAtPurchase(product.getPrice());
                orderItemRepository.save(item);
            }
        }

        return buildOrderResponse(order);
    }

    private OrderResponse buildOrderResponse(Order order) {
        List<OrderItem> items = orderItemRepository.findAllByOrderIdWithDetails(order.getId());

        Map<SellerOrder, List<OrderItem>> bySeller = items.stream()
                .collect(Collectors.groupingBy(
                        OrderItem::getSellerOrder,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<SellerOrderResponse> sellerOrderResponses = bySeller.entrySet().stream()
                .map(entry -> {
                    SellerOrder so = entry.getKey();
                    List<OrderItemResponse> itemResponses = entry.getValue().stream()
                            .map(oi -> new OrderItemResponse(
                                    oi.getProduct().getId(),
                                    oi.getProduct().getName(),
                                    oi.getQuantity(),
                                    oi.getPriceAtPurchase()
                            ))
                            .toList();

                    return new SellerOrderResponse(
                            so.getId(),
                            so.getSellerProfile().getId(),
                            so.getSellerProfile().getDisplayName(),
                            so.getStatus(),
                            itemResponses
                    );
                })
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getCreatedAt(),
                sellerOrderResponses
        );
    }
}