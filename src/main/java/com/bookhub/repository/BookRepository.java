// ===== 3. BookRepository =====
// src/main/java/com/bookhub/repository/BookRepository.java
package com.bookhub.repository;

import com.bookhub.entity.Book;
import com.bookhub.entity.BookStatus;
import com.bookhub.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Basic find methods
    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);

    // Find by category
    List<Book> findByCategory(Category category);
    List<Book> findByCategoryId(Long categoryId);
    Page<Book> findByCategory(Category category, Pageable pageable);

    // Find by status
    List<Book> findByStatus(BookStatus status);
    List<Book> findByStatusAndStockQuantityGreaterThan(BookStatus status, Integer stockQuantity);

    // Search methods
    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByAuthorContainingIgnoreCase(String author);
    List<Book> findByPublisherContainingIgnoreCase(String publisher);

    @Query("SELECT b FROM Book b WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Book> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT b FROM Book b WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Book> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Price range queries
    List<Book> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    Page<Book> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    List<Book> findByPriceLessThanEqual(BigDecimal maxPrice);
    List<Book> findByPriceGreaterThanEqual(BigDecimal minPrice);

    // Stock queries
    List<Book> findByStockQuantityGreaterThan(Integer quantity);
    List<Book> findByStockQuantityLessThanEqual(Integer quantity);
    List<Book> findByStockQuantityEquals(Integer quantity);

    // Publication date queries
    List<Book> findByPublicationDateAfter(LocalDate date);
    List<Book> findByPublicationDateBefore(LocalDate date);
    List<Book> findByPublicationDateBetween(LocalDate startDate, LocalDate endDate);

    // Combined filters
    @Query("SELECT b FROM Book b WHERE " +
            "(:categoryId IS NULL OR b.category.id = :categoryId) AND " +
            "(:minPrice IS NULL OR b.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR b.price <= :maxPrice) AND " +
            "(:inStock IS NULL OR (:inStock = true AND b.stockQuantity > 0) OR (:inStock = false)) AND " +
            "b.status = :status")
    Page<Book> findBooksWithFilters(@Param("categoryId") Long categoryId,
                                    @Param("minPrice") BigDecimal minPrice,
                                    @Param("maxPrice") BigDecimal maxPrice,
                                    @Param("inStock") Boolean inStock,
                                    @Param("status") BookStatus status,
                                    Pageable pageable);

    // Popular books (most ordered)
    @Query("SELECT b FROM Book b JOIN b.orderItems oi " +
            "GROUP BY b.id ORDER BY SUM(oi.quantity) DESC")
    List<Book> findMostPopularBooks(Pageable pageable);

    // New arrivals
    @Query("SELECT b FROM Book b WHERE b.createdAt >= :date ORDER BY b.createdAt DESC")
    List<Book> findNewArrivals(@Param("date") LocalDate date, Pageable pageable);

    // Low stock books
    @Query("SELECT b FROM Book b WHERE b.stockQuantity <= :threshold AND b.status = 'AVAILABLE'")
    List<Book> findLowStockBooks(@Param("threshold") Integer threshold);

    // Statistics queries
    @Query("SELECT COUNT(b) FROM Book b WHERE b.status = :status")
    long countByStatus(@Param("status") BookStatus status);

    @Query("SELECT AVG(b.price) FROM Book b WHERE b.status = 'AVAILABLE'")
    BigDecimal findAveragePrice();

    @Query("SELECT c.name, COUNT(b) FROM Book b JOIN b.category c " +
            "WHERE b.status = 'AVAILABLE' GROUP BY c.id, c.name ORDER BY COUNT(b) DESC")
    List<Object[]> findBookCountByCategory();

    // Featured books (you can customize this logic)
    @Query("SELECT b FROM Book b WHERE b.status = 'AVAILABLE' AND b.stockQuantity > 0 " +
            "ORDER BY b.createdAt DESC")
    List<Book> findFeaturedBooks(Pageable pageable);

    // Books by author
    List<Book> findByAuthorIgnoreCaseOrderByPublicationDateDesc(String author);

    // Find similar books (same category, different book)
    @Query("SELECT b FROM Book b WHERE b.category = :category AND b.id != :bookId " +
            "AND b.status = 'AVAILABLE' ORDER BY b.createdAt DESC")
    List<Book> findSimilarBooks(@Param("category") Category category,
                                @Param("bookId") Long bookId,
                                Pageable pageable);
}