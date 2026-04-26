package com.wcl.product.dto;

import java.math.BigDecimal;
import java.util.Map;

public record ProductResponseDTO(
        String id,
        String sku,
        String name,
        String description,
        BigDecimal basePrice,
        Integer stockQuantity,
        Map<String, String> technicalSpecifications
) {}