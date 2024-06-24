package com.example.ecommerce.service;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    public List<Category> getCategories(){
        return categoryRepository.findAll();
    }
    public List<Product> getProductsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found."));
        return productRepository.findProductsByCategory(category);
    }
    public Category save(Category category){
        return categoryRepository.save(category);
    }
    public Category update(Long id, Category category){
        category.setId(id);
        return categoryRepository.save(category);
    }
    public void deleteCategory(Long id){
        categoryRepository.deleteById(id);
    }
}
