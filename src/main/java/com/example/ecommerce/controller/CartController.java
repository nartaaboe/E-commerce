package com.example.ecommerce.controller;

import com.example.ecommerce.dto.response.MessageResponse;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.OrderService;
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
    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCartByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }
    @PutMapping("/{userId}/{cartItemId}")
    public ResponseEntity<Cart> increaseQuantity(@PathVariable Long userId, @PathVariable Long cartItemId) {
        return ResponseEntity.ok(cartService.increaseQuantity(userId, cartItemId));
    }
    @PostMapping("/{userId}")
    public ResponseEntity<MessageResponse> placeOrder(@PathVariable Long userId){
        orderService.placeOrder(userId);
        return ResponseEntity.ok(new MessageResponse("To complete order go on and put payment details."));
    }
    @DeleteMapping("/{userId}/{id}")
        public ResponseEntity<MessageResponse> removeFromCart(@PathVariable Long userId, @PathVariable Long id){
        cartService.deleteFromCart(userId, id);
        return ResponseEntity.ok(new MessageResponse("product deleted from cart."));
    }
}
