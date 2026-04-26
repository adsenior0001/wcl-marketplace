package com.wcl.user.dto;

public record LoginRequestDTO(
        String email,
        String password
) {}