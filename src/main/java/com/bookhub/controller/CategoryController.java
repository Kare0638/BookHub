// src/main/java/com/bookhub/controller/CategoryController.java
package com.bookhub.controller;

import com.bookhub.entity.Category;
import com.bookhub.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Get all categories
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // Get active categories only
    @GetMapping("/active")
    public ResponseEntity<List<Category>> getActiveCategories() {
        List<Category> categories = categoryService.getActiveCategories();
        return ResponseEntity.ok(categories);
    }

    // Get category by ID
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Optional<Category> category = categoryService.getCategoryById(id);
        return category.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Search categories
    @GetMapping("/search")
    public ResponseEntity<List<Category>> searchCategories(@RequestParam String keyword) {
        List<Category> categories = categoryService.searchCategories(keyword);
        return ResponseEntity.ok(categories);
    }

    // Create new category (Admin only)
    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        try {
            Category savedCategory = categoryService.saveCategory(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update category (Admin only)
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @Valid @RequestBody Category category) {
        try {
            Category updatedCategory = categoryService.updateCategory(id, category);
            return ResponseEntity.ok(updatedCategory);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Toggle category status (Admin only)
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<Void> toggleCategoryStatus(@PathVariable Long id) {
        try {
            Optional<Category> categoryOpt = categoryService.getCategoryById(id);
            if (categoryOpt.isPresent()) {
                Category category = categoryOpt.get();
                if (category.isActive()) {
                    categoryService.deactivateCategory(id);
                } else {
                    categoryService.activateCategory(id);
                }
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete category (Admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get categories with books
    @GetMapping("/with-books")
    public ResponseEntity<List<Category>> getCategoriesWithBooks() {
        List<Category> categories = categoryService.getCategoriesWithBooks();
        return ResponseEntity.ok(categories);
    }

    // Get category statistics (Admin only)
    @GetMapping("/stats")
    public ResponseEntity<Object> getCategoryStatistics() {
        return ResponseEntity.ok(new Object() {
            public final long totalCategories = categoryService.getTotalCategoryCount();
            public final long activeCategories = categoryService.getActiveCategoryCount();
            public final List<Object[]> bookCounts = categoryService.getCategoryBookCounts();
        });
    }
}