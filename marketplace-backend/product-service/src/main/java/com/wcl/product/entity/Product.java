package com.wcl.product.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Map;

@Document(collection = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    private String id; // MongoDB uses String for its default ObjectID

    private String sku; // e.g., "SKF-6204-2Z"

    private String name;

    private String description;

    private BigDecimal basePrice;

    private Integer stockQuantity;

    // This is the magic of NoSQL: We can store any dynamic attributes without altering tables!
    private Map<String, String> technicalSpecifications;
}