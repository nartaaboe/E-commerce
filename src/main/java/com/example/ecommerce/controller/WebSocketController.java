package com.example.ecommerce.controller;

import com.example.ecommerce.entity.CartItem;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @MessageMapping("/update")
    @SendTo("/topic/cart")
    public CartItem sendUpdate(CartItem cartItem) {
        return cartItem;
    }

}
