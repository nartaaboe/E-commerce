package com.example.ecommerce.service.kafka.consumer;

import com.example.ecommerce.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    @Autowired
    public ProductEventProducer(KafkaTemplate<String, Object> kafkaTemplate, SimpMessagingTemplate messagingTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.messagingTemplate = messagingTemplate;
    }
    public void sendProductEvent(Product product) {
        kafkaTemplate.send("product-events", product);
        messagingTemplate.convertAndSend("/topic/product", product);
    }
}
