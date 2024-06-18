package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.PaymentDetailsRequest;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.repository.*;
import com.example.ecommerce.service.kafka.consumer.OrderEventProducer;
import com.example.ecommerce.service.kafka.consumer.ProductEventProducer;
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
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderEventProducer orderEventProducer;
    @Autowired
    private ProductEventProducer productEventProducer;
    @Autowired
    private CartItemRepository cartItemRepository;

    public List<Order> getAllOrders(){
        return orderRepository.findAll();
    }
    public List<Order> getAllOrdersByUserId(Long userId) {
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
        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty!");
        }

        Order order = new Order();
        order.setOrderItems(new ArrayList<>());
        order.setUser(user);

        // Create a copy of the cart items to avoid ConcurrentModificationException
        List<CartItem> cartItemsCopy = new ArrayList<>(cart.getCartItems());

        for (CartItem cartItem : cartItemsCopy) {
            OrderItem orderItem = new OrderItem();
            Product product = cartItem.getProduct();
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());

            // Update product quantity
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            // Produce product event
            productEventProducer.sendProductEvent(product);

            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
            orderItemRepository.save(orderItem);

            // Remove cart item and delete it
            cart.getCartItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        }

        if (user.getOrders() == null) {
            user.setOrders(new ArrayList<>());
        }
        user.getOrders().add(order);

        // Save order and user
        orderRepository.save(order);
        userRepository.save(user);

        // Produce order event
        orderEventProducer.sendOrderEvent(order);
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
