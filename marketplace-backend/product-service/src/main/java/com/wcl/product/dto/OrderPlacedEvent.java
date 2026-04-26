package com.wcl.product.dto;

public record OrderPlacedEvent(String orderId, String sku, Integer quantity) {}