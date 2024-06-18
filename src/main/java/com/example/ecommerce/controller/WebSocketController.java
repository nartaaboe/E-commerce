package com.example.ecommerce.controller;

import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.Product;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @MessageMapping("/cart")
    @SendTo("/topic/cart")
    public CartItem sendUpdate(CartItem cartItem) {
        return cartItem;
    }
    @MessageMapping("/order")
    @SendTo("/topic/order")
    public Order orderUpdate(Order order) {
        return order;
    }
    @MessageMapping("/product")
    @SendTo("/topic/product")
    public Product productUpdate(Product product) {
        return product;
    }
}

