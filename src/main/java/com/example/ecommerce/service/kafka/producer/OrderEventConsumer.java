package com.example.ecommerce.service.kafka.producer;

import com.example.ecommerce.controller.WebSocketController;
import com.example.ecommerce.entity.Order;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderEventConsumer {
    private final WebSocketController webSocketController;

    public OrderEventConsumer(WebSocketController webSocketController) {
        this.webSocketController = webSocketController;
    }
    @KafkaListener(topics = "order-events", groupId = "ecommerce-group")
    public void consumeOrderEvent(Order order){
        System.out.println("Received order event: " + order);
        webSocketController.orderUpdate(order);
    }
}
