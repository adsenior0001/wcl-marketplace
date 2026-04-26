package com.wcl.product.dto;

import java.util.UUID;

// This record must exactly match the structure sent by the User Service
public record UserCreatedEvent(
        UUID userId,
        String email,
        String companyName
) {}