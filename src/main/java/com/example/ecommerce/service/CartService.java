package com.example.ecommerce.service;

import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.kafka.consumer.CartEventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Transactional
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartEventProducer cartEventProducer;
    @Autowired
    private CartItemRepository cartItemRepository;
    public void  addToCart(Long userId, Product product) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = user.getCart();
        CartItem cartItem = new CartItem();
        if(product.getQuantity() < 1)
            throw new RuntimeException("Product quantity is less than zero");
        cartItem.setProduct(product);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setCartItems(new ArrayList<>());
            cart.getCartItems().add(cartItem);
            user.setCart(cart);
            userRepository.save(user);
        }
        else{
            cart.getCartItems().add(cartItem);
        }
        cartItem.setCart(cart);
        cartItem.setQuantity(1);
        cartItemRepository.save(cartItem);
        cartRepository.save(cart);
    }
    public void increaseQuantity(Long userId, Long cartItemId){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = user.getCart();
        CartItem item = new CartItem();
        boolean itemFound = false;
        for(CartItem cartItem : cart.getCartItems()){
            if(cartItem.getId().equals(cartItemId)){
                if(cartItem.getQuantity() == cartItem.getProduct().getQuantity()){
                    throw new RuntimeException("Product quantity reach max available quantity.");
                }
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                item = cartItem;
                cartItemRepository.save(cartItem);
                itemFound = true;
                break;
            }
        }
        if (!itemFound) {
            throw new RuntimeException("Cart item not found");
        }
        cartRepository.save(cart);
        cartEventProducer.sendCartEvent(item);
    }
    public void decreaseQuantity(Long userId, Long cartItemId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Cart cart = user.getCart();
        CartItem item = new CartItem();
        boolean itemFound = false;
        for(CartItem cartItem : cart.getCartItems()){
            if(cartItem.getId().equals(cartItemId)){
                cartItem.setQuantity(cartItem.getQuantity() - 1);
                item = cartItem;
                cartItemRepository.save(cartItem);
                itemFound = true;
                break;
            }
        }
        if (!itemFound) {
            throw new RuntimeException("Cart item not found");
        }
        cartRepository.save(cart);
        cartEventProducer.sendCartEvent(item);
    }
    public Cart getCartByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(User::getCart).orElseThrow(() -> new RuntimeException("User not found"));
    }
    public void deleteFromCart(Long userId, Long id) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        CartItem cartItem = cartItemRepository.findById(id).orElseThrow(() -> new RuntimeException("CartItem not found"));
        user.getCart().getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        userRepository.save(user);
    }
}
