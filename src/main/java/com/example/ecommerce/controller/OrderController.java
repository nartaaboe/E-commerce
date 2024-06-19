package com.example.ecommerce.controller;

import com.example.ecommerce.dto.response.MessageResponse;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderStatus;
import com.example.ecommerce.entity.PaymentMethod;
import com.example.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @GetMapping
    public ResponseEntity<List<Order>> getOrders(){
        return ResponseEntity.ok(orderService.getAllOrders());
    }
    @GetMapping("/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable Long userId){
        return ResponseEntity.ok(orderService.getAllOrdersByUserId(userId));
    }
    @GetMapping("/{userId}/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long userId, @PathVariable Long id){
        return ResponseEntity.ok(orderService.getOrderById(userId, id));
    }
    @DeleteMapping("/{userId}/{id}")
    public ResponseEntity<MessageResponse> deleteOrder(@PathVariable Long userId, @PathVariable Long id){
        orderService.deleteOrder(userId, id);
        return ResponseEntity.ok(new MessageResponse("Order deleted successfully"));
    }
    @PutMapping("/{userId}/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long userId, @PathVariable Long id,
                                                   @RequestBody OrderStatus orderStatus){
        return ResponseEntity.ok(orderService.updateOrderStatus(userId, id, orderStatus));
    }
    @PutMapping("/{userId}/{id}/payment")
    public ResponseEntity<Order> putPaymentDetails(@PathVariable Long userId, @PathVariable Long id,
                                                   @RequestBody PaymentMethod paymentMethod){
        return ResponseEntity.ok(orderService.putPaymentDetails(userId, id, paymentMethod));
    }
}
