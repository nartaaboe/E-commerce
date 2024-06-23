package com.example.ecommerce.controller;

import com.example.ecommerce.dto.response.MessageResponse;
import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.service.CategoryService;
import com.example.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("categories")
@RestController
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping
    public ResponseEntity<List<Category>> getCategories() {
        return ResponseEntity.ok(categoryService.getCategories());
    }
    @GetMapping("/{id}")
    public ResponseEntity<List<Product>> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getProductsByCategory(id));
    }
    @PostMapping
    public ResponseEntity<Category> saveCategory(@RequestBody Category category){
        return ResponseEntity.ok(categoryService.save(category));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category category){
        return ResponseEntity.ok(categoryService.update(id, category));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteCategory(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(new MessageResponse("category deleted successfully."));
    }
}
