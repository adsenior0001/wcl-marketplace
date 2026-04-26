package com.wcl.order.controller;

import com.wcl.order.dto.OrderRequestDTO;
import com.wcl.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<String> placeOrder(
            @Valid @RequestBody OrderRequestDTO request,
            @RequestHeader(value = "X-User-Email", required = false) String userEmail) {

        // If the Gateway didn't attach the email, we reject it (Security)
        if (userEmail == null || userEmail.isEmpty()) {
            return ResponseEntity.status(401).body("Unauthorized: Missing User Email Header");
        }

        String orderId = orderService.placeOrder(request, userEmail);
        return ResponseEntity.ok("Order successfully placed with ID: " + orderId);
    }
}