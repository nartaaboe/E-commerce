package com.example.ecommerce.service.kafka.producer;

import com.example.ecommerce.controller.WebSocketController;
import com.example.ecommerce.entity.Product;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ProductEventConsumer {
    private final WebSocketController webSocketController;

    public ProductEventConsumer(WebSocketController webSocketController) {
        this.webSocketController = webSocketController;
    }

    @KafkaListener(topics = "product-events", groupId = "ecommerce-group")
    public void consumeProductEvent(Product product){
        System.out.println("Received product event: " + product);
        webSocketController.productUpdate(product);
    }
}
