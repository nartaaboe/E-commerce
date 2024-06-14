package com.example.ecommerce.service.kafka.consumer;

import com.example.ecommerce.entity.CartItem;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CartEventProducer {

    private final KafkaTemplate<String, CartItem> kafkaTemplate;

    public CartEventProducer(KafkaTemplate<String, CartItem> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendCartEvent(CartItem cartItem) {
        kafkaTemplate.send("cart-events", cartItem);
    }
}
