package com.wcl.user.kafka;

import com.wcl.user.dto.UserCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserProducer {

    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

    public UserProducer(KafkaTemplate<String, UserCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserCreatedEvent(UserCreatedEvent event) {
        // "user-created" is the name of the Kafka Topic
        kafkaTemplate.send("user-created", event);
        System.out.println(">>> KAFKA PUBLISHER: Shouting about new user: " + event.email());
    }
}