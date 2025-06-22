// ===== 3. CategoryService =====
// src/main/java/com/bookhub/service/CategoryService.java
package com.bookhub.service;

import com.bookhub.entity.Category;
import com.bookhub.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Basic CRUD operations
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> getActiveCategories() {
        return categoryRepository.findByActiveTrueOrderByNameAsc();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    public Category saveCategory(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new IllegalArgumentException("Category with this name already exists");
        }
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category updatedCategory) {
        return categoryRepository.findById(id)
                .map(category -> {
                    category.setName(updatedCategory.getName());
                    category.setDescription(updatedCategory.getDescription());
                    category.setActive(updatedCategory.isActive());
                    return categoryRepository.save(category);
                })
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (category.getBookCount() > 0) {
            throw new IllegalArgumentException("Cannot delete category with existing books");
        }

        categoryRepository.deleteById(id);
    }

    // Search operations
    public List<Category> searchCategories(String keyword) {
        return categoryRepository.searchByKeyword(keyword);
    }

    public List<Category> searchCategoriesByName(String name) {
        return categoryRepository.findByNameContainingIgnoreCase(name);
    }

    // Status management
    public void activateCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        category.setActive(true);
        categoryRepository.save(category);
    }

    public void deactivateCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        category.setActive(false);
        categoryRepository.save(category);
    }

    // Validation
    public boolean isCategoryNameAvailable(String name) {
        return !categoryRepository.existsByName(name);
    }

    // Categories with/without books
    public List<Category> getCategoriesWithBooks() {
        return categoryRepository.findCategoriesWithBooks();
    }

    public List<Category> getCategoriesWithoutBooks() {
        return categoryRepository.findCategoriesWithoutBooks();
    }

    // Statistics
    public long getTotalCategoryCount() {
        return categoryRepository.count();
    }

    public long getActiveCategoryCount() {
        return categoryRepository.countByActiveTrue();
    }

    public List<Object[]> getCategoryBookCounts() {
        return categoryRepository.findCategoryBookCounts();
    }
}