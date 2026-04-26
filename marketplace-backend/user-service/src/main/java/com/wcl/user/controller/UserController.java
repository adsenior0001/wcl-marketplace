package com.wcl.user.controller;

import com.wcl.user.dto.AuthResponse;
import com.wcl.user.dto.LoginRequestDTO;
import com.wcl.user.dto.RegisterRequestDTO;
import com.wcl.user.dto.UserResponseDTO;
import com.wcl.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // -------------------------------------------------------------------
    // 1. REGISTRATION ENDPOINT (POST /api/v1/users/register)
    // -------------------------------------------------------------------
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        System.out.println("--- RECEIVED REGISTRATION REQUEST FOR EMAIL: " + request.email() + " ---");

        UserResponseDTO response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // -------------------------------------------------------------------
    // 2. LOGIN ENDPOINT (POST /api/v1/users/login)
    // -------------------------------------------------------------------
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequestDTO request) {
        System.out.println("--- RECEIVED LOGIN REQUEST FOR EMAIL: " + request.email() + " ---");

        AuthResponse response = userService.login(request);
        return ResponseEntity.ok(response); // 200 OK is standard for successful logins
    }
}