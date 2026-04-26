package com.wcl.product.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcl.product.dto.OrderPlacedEvent;
import com.wcl.product.service.ProductService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderEventConsumer {

    private final ProductService productService;
    private final ObjectMapper objectMapper; // Jackson JSON Parser

    public OrderEventConsumer(ProductService productService, ObjectMapper objectMapper) {
        this.productService = productService;
        this.objectMapper = objectMapper;
    }

    // Matches the group-id in your YAML
    @KafkaListener(topics = "order-events", groupId = "product-service-group")
    public void handleOrderPlacedEvent(String rawJsonMessage) {
        try {
            // 1. Safely convert the raw string into our Java Record
            OrderPlacedEvent event = objectMapper.readValue(rawJsonMessage, OrderPlacedEvent.class);

            System.out.println(">>> RECEIVED KAFKA EVENT: Decrementing inventory for SKU: " + event.sku());

            // 2. Execute business logic
            productService.decrementInventory(event.sku(), event.quantity());

        } catch (Exception e) {
            System.err.println("<<< KAFKA ERROR: Failed to process message. Skipping to prevent loop. Message: " + rawJsonMessage);
            // By catching the exception, we prevent the infinite Poison Pill loop!
            e.printStackTrace();
        }
    }
}