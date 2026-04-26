package com.wcl.product.kafka;

import com.wcl.product.dto.UserCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserEventListener {

    // Automatically connects to Kafka and listens to the specific topic
    @KafkaListener(topics = "user-created", groupId = "product-service-group")
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        System.out.println("<<< KAFKA SUBSCRIBER: Product Service heard about new user!");
        System.out.println("<<< PREPARING CATALOG FOR: " + event.companyName());

        // Future Logic: Trigger database generation of SKF products specific to this company
    }
}