package com.example.ecommerce.service;

import com.example.ecommerce.entity.*;
import com.example.ecommerce.repository.*;
import com.example.ecommerce.service.kafka.consumer.CartEventProducer;
import com.example.ecommerce.service.kafka.consumer.OrderEventProducer;
import com.example.ecommerce.service.kafka.consumer.ProductEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    private CartEventProducer cartEventProducer;
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
    public Order placeOrder(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found!"));
        Cart cart = user.getCart();
        if (cart == null || cart.getCartItems().isEmpty()) {
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

        if (user.getOrders() == null) {
            user.setOrders(new ArrayList<>());
        }
        user.getOrders().add(order);
        orderRepository.save(order);
        userRepository.save(user);

        orderEventProducer.sendOrderEvent(order);
        return order;
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
        orderItemRepository.deleteAll(order.getOrderItems());
        orderRepository.delete(order);
    }

    public Order putPaymentDetails(Long userId, Long id, PaymentMethod paymentMethod) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found!"));
        Order order = user.getOrders().stream().filter(o -> o.getId().equals(id)).findFirst().orElseThrow(() -> new RuntimeException("Order not found!"));
        Payment payment = new Payment();
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus(PaymentStatus.COMPLETED);
        order.setPayment(payment);
        order.setStatus(OrderStatus.PENDING);
        payment.setOrder(order);
        for(OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            order.getUser().getCart().getCartItems().removeIf(cartItem -> cartItem.getProduct().equals(product));
            product.setQuantity(product.getQuantity() - orderItem.getQuantity());
            productEventProducer.sendProductEvent(product);
        }
        orderEventProducer.sendOrderEvent(order);
        paymentRepository.save(payment);
        orderRepository.save(order);
        return order;
    }
}
