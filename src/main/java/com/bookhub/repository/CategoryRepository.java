// ===== 2. CategoryRepository =====
// src/main/java/com/bookhub/repository/CategoryRepository.java
package com.bookhub.repository;

import com.bookhub.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Basic find methods
    Optional<Category> findByName(String name);
    boolean existsByName(String name);

    // Find active/inactive categories
    List<Category> findByActiveTrue();
    List<Category> findByActiveFalse();
    List<Category> findByActiveTrueOrderByNameAsc();

    // Search categories
    List<Category> findByNameContainingIgnoreCase(String name);
    List<Category> findByDescriptionContainingIgnoreCase(String description);

    @Query("SELECT c FROM Category c WHERE " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Category> searchByKeyword(@Param("keyword") String keyword);

    // Categories with books
    @Query("SELECT c FROM Category c WHERE SIZE(c.books) > 0")
    List<Category> findCategoriesWithBooks();

    @Query("SELECT c FROM Category c WHERE SIZE(c.books) = 0")
    List<Category> findCategoriesWithoutBooks();

    // Count methods
    long countByActiveTrue();

    @Query("SELECT c.name, COUNT(b) FROM Category c LEFT JOIN c.books b GROUP BY c.id, c.name")
    List<Object[]> findCategoryBookCounts();
}
