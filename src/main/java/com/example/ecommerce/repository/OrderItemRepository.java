package com.example.ecommerce.repository;

import com.example.ecommerce.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("""
        SELECT oi FROM OrderItem oi
        JOIN FETCH oi.sellerOrder so
        JOIN FETCH so.sellerProfile sp
        JOIN FETCH oi.product p
        WHERE so.order.id = :orderId
        ORDER BY so.id, oi.id
        """)
    List<OrderItem> findAllByOrderIdWithDetails(Long orderId);

    @Query("""
        SELECT oi FROM OrderItem oi
        JOIN FETCH oi.sellerOrder so
        JOIN FETCH oi.product p
        WHERE so.sellerProfile.id = :sellerProfileId
        ORDER BY so.createdAt DESC, oi.id
        """)
    List<OrderItem> findAllBySellerProfileIdWithDetails(Long sellerProfileId);

}
