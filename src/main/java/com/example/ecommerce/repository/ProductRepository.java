package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.SellerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findBySellerProfile(SellerProfile sellerProfile);
    List<Product> findBySellerProfileId(Long sellerProfileId);
    List<Product> findByActiveTrue();

}
