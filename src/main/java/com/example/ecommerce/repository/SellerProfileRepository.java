package com.example.ecommerce.repository;

import com.example.ecommerce.entity.SellerProfile;
import com.example.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerProfileRepository extends JpaRepository<SellerProfile, Long> {
    Optional<SellerProfile> findByUser(User user);
    Optional<SellerProfile> findByUserId(Long userId);
}
