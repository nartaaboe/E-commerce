package com.example.ecommerce.service.kafka.producer;

import com.example.ecommerce.controller.WebSocketController;
import com.example.ecommerce.entity.CartItem;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class CartEventConsumer {

    private final WebSocketController webSocketController;

    public CartEventConsumer(WebSocketController webSocketController) {
        this.webSocketController = webSocketController;
    }

    @KafkaListener(topics = "cart-events", groupId = "ecommerce-group")
    public void consumeCartEvent(CartItem cartItem) {
        System.out.println("Received Cart Item Event: " + cartItem);
        webSocketController.sendUpdate(cartItem);
    }
}
