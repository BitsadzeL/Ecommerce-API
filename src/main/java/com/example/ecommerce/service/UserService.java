package com.example.ecommerce.service;

import com.example.ecommerce.dto.RegisterRequest;
import com.example.ecommerce.dto.RegisterResponse;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.exception.EmailAlreadyExistsException;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new EmailAlreadyExistsException(
                    "Email " + request.email() + " is already used. Try another one");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setPhoneNumber(request.phoneNumber());

        User saved = userRepository.save(user);

        return new RegisterResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getPhoneNumber(),
                saved.getCreatedAt()
        );
    }
}