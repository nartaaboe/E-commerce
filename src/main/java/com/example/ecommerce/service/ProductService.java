package com.example.ecommerce.service;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    public List<Product> findAll() {
        return productRepository.findAll();
    }
    @Cacheable(value = "product", key = "#id")
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }
    @CacheEvict(value = "product", key = "#product.name")
    public void save(Product product) {
        productRepository.save(product);
    }
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
    public Product update(Long id, Product product){
        product.setId(id);
        return productRepository.save(product);
    }
}
