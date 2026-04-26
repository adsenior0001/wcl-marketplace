package com.wcl.order.service;

import com.wcl.order.dto.OrderPlacedEvent;
import com.wcl.order.dto.OrderRequestDTO;
import com.wcl.order.entity.Order;
import com.wcl.order.kafka.OrderProducer;
import com.wcl.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;

    public OrderService(OrderRepository orderRepository, OrderProducer orderProducer) {
        this.orderRepository = orderRepository;
        this.orderProducer = orderProducer;
    }

    @Transactional
    public String placeOrder(OrderRequestDTO request, String userEmail) {
        // 1. Create and save the order to PostgreSQL
        Order order = Order.builder()
                .userEmail(userEmail)
                .sku(request.sku())
                .quantity(request.quantity())
                .status("PENDING") // Starts as pending until inventory is confirmed
                .build();

        Order savedOrder = orderRepository.save(order);

        // 2. Create the Kafka Event
        OrderPlacedEvent event = new OrderPlacedEvent(
                savedOrder.getId(),
                savedOrder.getSku(),
                savedOrder.getQuantity()
        );

        // 3. Send it to the Product Service
        orderProducer.sendOrderEvent(event);

        // 4. Return the new Order ID to the user immediately
        return savedOrder.getId();
    }
}