package com.example.ecommerce.controller;

import com.example.ecommerce.dto.response.MessageResponse;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.service.CartService;
import com.example.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CartService cartService;
    @GetMapping
    public ResponseEntity<List<Product>> getProducts() {
        return ResponseEntity.ok(productService.findAll());
    }
    @PostMapping
    public ResponseEntity<MessageResponse> addProduct(@RequestBody Product product) {
        productService.save(product);
        return ResponseEntity.ok(new MessageResponse("Saved product successfully."));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteProduct(@PathVariable Long id) {
        productService.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Deleted product successfully."));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id).orElse(null));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return ResponseEntity.ok(productService.update(id, product));
    }
    @PostMapping("/{userId}")
    public ResponseEntity<MessageResponse> addToCart(@PathVariable Long userId, @RequestBody Product product) {
        cartService.addToCart(userId, product);
        return ResponseEntity.ok(new MessageResponse("saved to cart."));
    }
}
