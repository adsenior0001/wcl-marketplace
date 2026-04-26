package com.wcl.order.dto;

public record OrderPlacedEvent(
        String orderId,
        String sku,
        Integer quantity
) {}