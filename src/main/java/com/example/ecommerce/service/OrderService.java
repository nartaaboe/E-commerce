package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.PaymentDetailsRequest;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    public List<Order> getAllOrders(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getOrders();
    }

    public Order getOrderById(Long userId, Long id) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getOrders().stream().filter(o -> o.getId().equals(id)).findFirst().orElse(null);
    }
    public void placeOrder(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found!"));
        Cart cart = user.getCart();
        if(cart == null || cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty!");
        }
        Order order = new Order();
        order.setOrderItems(new ArrayList<>());
        order.setUser(user);
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
            orderItemRepository.save(orderItem);
        }
        if(user.getOrders() == null){
            user.setOrders(new ArrayList<>());
            user.getOrders().add(order);
        }
        user.getOrders().add(order);
        orderRepository.save(order);
        userRepository.save(user);
    }
    public Order updateOrderStatus(Long userId, Long id, OrderStatus orderStatus) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found!"));
        Order order = user.getOrders().stream().filter(o -> o.getId().equals(id)).findFirst().orElseThrow(() -> new RuntimeException("Order not found!"));
        order.setStatus(orderStatus);
        orderRepository.save(order);
        return order;
    }
    public void deleteOrder(Long userId, Long id) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found!"));
        Order order = user.getOrders().stream().filter(o -> o.getId().equals(id)).findFirst().orElseThrow(() -> new RuntimeException("Order not found!"));
        for(OrderItem orderItem : order.getOrderItems()) {
            orderItemRepository.delete(orderItem);
        }
        orderRepository.delete(order);
    }

    public Order putPaymentDetails(Long userId, Long id, PaymentDetailsRequest paymentDetailsRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found!"));
        Order order = user.getOrders().stream().filter(o -> o.getId().equals(id)).findFirst().orElseThrow(() -> new RuntimeException("Order not found!"));
        Payment payment = new Payment();
        payment.setPaymentMethod(paymentDetailsRequest.getPaymentMethod());
        payment.setStatus(paymentDetailsRequest.getStatus());
        order.setPayment(payment);
        payment.setOrder(order);
        orderRepository.save(order);
        paymentRepository.save(payment);
        return order;
    }
}
