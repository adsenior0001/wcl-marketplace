package com.wcl.order.kafka;

import com.wcl.order.dto.OrderPlacedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public OrderProducer(KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderEvent(OrderPlacedEvent event) {
        // We use the topic name "order-events"
        kafkaTemplate.send("order-events", event.orderId(), event);
        System.out.println(">>> KAFKA EVENT PUBLISHED: Order placed for SKU: " + event.sku());
    }
}