package com.wcl.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDTO(
        @NotBlank(message = "Email is required")
        @Email(message = "Must be a valid email format")
        String email,

        @NotBlank(message = "Company name is required")
        String companyName,

        @NotBlank(message = "Role is required")
        String role
) {}