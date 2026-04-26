package com.wcl.user.service;

import com.wcl.user.dto.AuthResponse;
import com.wcl.user.dto.LoginRequestDTO;
import com.wcl.user.dto.RegisterRequestDTO;
import com.wcl.user.dto.UserCreatedEvent;
import com.wcl.user.dto.UserResponseDTO;
import com.wcl.user.entity.User;
import com.wcl.user.kafka.UserProducer;
import com.wcl.user.repository.UserRepository;
import com.wcl.user.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserProducer userProducer;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserProducer userProducer, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userProducer = userProducer;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    // -------------------------------------------------------------------
    // REGISTRATION FLOW (Replaces your old createUser method)
    // -------------------------------------------------------------------
    public UserResponseDTO register(RegisterRequestDTO request) {
        // 1. Business Logic: Check if user already exists
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("A user with this email already exists!");
        }

        // 2. Map DTO to Entity AND Hash the Password
        User newUser = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password())) // SECURE HASHING ADDED
                .companyName(request.companyName())
                .role(request.role() != null ? request.role() : "DISTRIBUTOR")
                .build();

        // 3. Save to Database (PostgreSQL)
        User savedUser = userRepository.save(newUser);

        // 4. Publish the event to Kafka (Kept exactly as you had it!)
        try {
            userProducer.sendUserCreatedEvent(new UserCreatedEvent(
                    savedUser.getId(),
                    savedUser.getEmail(),
                    savedUser.getCompanyName()
            ));
        } catch (Exception e) {
            System.err.println(">>> ERROR: Failed to send Kafka event: " + e.getMessage());
        }

        // 5. Generate the token right before returning
        String jwtToken = jwtService.generateToken(savedUser.getEmail());

        // 6. Map Entity back to Response DTO
        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getCompanyName(),
                savedUser.getRole(),
                savedUser.getCreatedAt(),
                jwtToken
        );
    }

    // -------------------------------------------------------------------
    // LOGIN FLOW (Brand new method)
    // -------------------------------------------------------------------
    public AuthResponse login(LoginRequestDTO request) {
        // 1. Find the user by email
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // 2. Verify raw password against the hashed database entry
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // 3. Generate new JWT Token
        String token = jwtService.generateToken(user.getEmail());

        // 4. Return just the token (Login doesn't need to return full user data)
        return new AuthResponse(token);
    }
}