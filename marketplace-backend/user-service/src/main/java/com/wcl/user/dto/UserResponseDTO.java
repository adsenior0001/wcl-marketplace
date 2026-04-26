package com.wcl.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

// Notice this doesn't have validation annotations, because this is what WE send OUT.
public record UserResponseDTO(
        UUID id,
        String email,
        String companyName,
        String role,
        LocalDateTime createdAt,
        String token
) {}