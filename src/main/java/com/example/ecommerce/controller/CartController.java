package com.example.ecommerce.controller;

import com.example.ecommerce.dto.response.MessageResponse;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.service.kafka.consumer.CartEventProducer;
import com.example.ecommerce.service.kafka.consumer.OrderEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderEventProducer orderEventProducer;
    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCartByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }
    @PutMapping("/{userId}/{cartItemId}/increase")
    public ResponseEntity<MessageResponse> increaseQuantity(@PathVariable Long userId, @PathVariable Long cartItemId) {
        cartService.increaseQuantity(userId, cartItemId);
        return ResponseEntity.ok(new MessageResponse("quantity increased."));
    }
    @PutMapping("/{userId}/{cartItemId}/decrease")
    public ResponseEntity<MessageResponse> decreaseQuantity(@PathVariable Long userId, @PathVariable Long cartItemId) {
        cartService.decreaseQuantity(userId, cartItemId);
        return ResponseEntity.ok(new MessageResponse("decrease quantity."));
    }
    @PostMapping("/{userId}")
    public ResponseEntity<Order> placeOrder(@PathVariable Long userId){
        return ResponseEntity.ok(orderService.placeOrder(userId));
    }
    @DeleteMapping("/{userId}/{id}")
        public ResponseEntity<MessageResponse> removeFromCart(@PathVariable Long userId, @PathVariable Long id){
        cartService.deleteFromCart(userId, id);
        return ResponseEntity.ok(new MessageResponse("product deleted from cart."));
    }
}
