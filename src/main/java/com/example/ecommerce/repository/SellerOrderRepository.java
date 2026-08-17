package com.example.ecommerce.repository;

import com.example.ecommerce.entity.SellerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SellerOrderRepository extends JpaRepository<SellerOrder, Long> {
    List<SellerOrder> findBySellerProfileIdOrderByCreatedAtDesc(Long sellerProfileId);
    List<SellerOrder> findByOrderId(Long orderId);
}
