package com.example.ecommerce.service.kafka.consumer;

import com.example.ecommerce.entity.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class CartEventProducer {

    private final KafkaTemplate<String, CartItem> kafkaTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public CartEventProducer(KafkaTemplate<String, CartItem> kafkaTemplate, SimpMessagingTemplate messagingTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.messagingTemplate = messagingTemplate;
    }

    public void sendCartEvent(CartItem cartItem) {
        kafkaTemplate.send("cart-events", cartItem);
        messagingTemplate.convertAndSend("/topic/cart", cartItem);
    }
}