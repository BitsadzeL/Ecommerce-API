package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.SellerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findBySellerProfile(SellerProfile sellerProfile);
    List<Product> findBySellerProfileId(Long sellerProfileId);
    List<Product> findByActiveTrue();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT p FROM Product p
        WHERE p.id = :id
        """)
    Optional<Product> findByIdWithLock(@Param("id") Long productId);
}
