package com.wcl.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.Map;

public record ProductRequestDTO(
        @NotBlank String sku,
        @NotBlank String name,
        String description,
        @NotNull @PositiveOrZero BigDecimal basePrice,
        @NotNull @PositiveOrZero Integer stockQuantity,
        Map<String, String> technicalSpecifications
) {}