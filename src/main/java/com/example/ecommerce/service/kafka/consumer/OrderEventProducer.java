package com.example.ecommerce.service.kafka.consumer;

import com.example.ecommerce.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    @Autowired
    public OrderEventProducer(KafkaTemplate<String, Object> kafkaTemplate, SimpMessagingTemplate messagingTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.messagingTemplate = messagingTemplate;
    }
    public void sendOrderEvent(Order order) {
        kafkaTemplate.send("order-events", order);
        messagingTemplate.convertAndSend("/topic/order", order);
    }
}
