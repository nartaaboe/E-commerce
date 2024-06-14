package com.example.ecommerce.service.kafka.producer;

import com.example.ecommerce.entity.CartItem;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class CartEventConsumer {

    @KafkaListener(topics = "cart-events", groupId = "ecommerce-group")
    public void consumeCartEvent(CartItem cartItem) {
        System.out.println("Received Cart Item Event: " + cartItem);
    }
}