package com.example.ecommerce.service;

import com.example.ecommerce.dto.BecomeSellerRequest;
import com.example.ecommerce.dto.SellerProfileResponse;
import com.example.ecommerce.entity.SellerProfile;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.SellerProfileAlreadyExistsException;
import com.example.ecommerce.exception.UserNotFoundException;
import com.example.ecommerce.repository.SellerProfileRepository;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class SellerProfileService {

    private final SellerProfileRepository sellerProfileRepository;
    private final UserRepository userRepository;

    public SellerProfileService(SellerProfileRepository sellerProfileRepository,
                                UserRepository userRepository) {
        this.sellerProfileRepository = sellerProfileRepository;
        this.userRepository = userRepository;
    }

    public SellerProfileResponse becomeSeller(Long userId, BecomeSellerRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        if (sellerProfileRepository.findByUserId(userId).isPresent()) {
            throw new SellerProfileAlreadyExistsException(
                    "User " + userId + " is already a seller");
        }

        SellerProfile profile = new SellerProfile();
        profile.setUser(user);
        profile.setDisplayName(request.displayName());

        SellerProfile saved = sellerProfileRepository.save(profile);

        return new SellerProfileResponse(
                saved.getId(),
                saved.getUser().getId(),
                saved.getDisplayName(),
                saved.getCreatedAt()
        );
    }
}