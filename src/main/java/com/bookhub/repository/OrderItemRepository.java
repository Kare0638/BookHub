// ===== 2. OrderItemRepository =====
// src/main/java/com/bookhub/repository/OrderItemRepository.java
package com.bookhub.repository;

import com.bookhub.entity.Book;
import com.bookhub.entity.Order;
import com.bookhub.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Find by order
    List<OrderItem> findByOrder(Order order);
    List<OrderItem> findByOrderId(Long orderId);

    // Find by book
    List<OrderItem> findByBook(Book book);
    List<OrderItem> findByBookId(Long bookId);

    // Find by order and book
    OrderItem findByOrderAndBook(Order order, Book book);
    OrderItem findByOrderIdAndBookId(Long orderId, Long bookId);

    // Most popular books (by quantity sold)
    @Query("SELECT oi.book, SUM(oi.quantity) as totalSold FROM OrderItem oi " +
            "JOIN oi.order o WHERE o.status = 'DELIVERED' " +
            "GROUP BY oi.book ORDER BY totalSold DESC")
    List<Object[]> findMostPopularBooksByQuantity();

    // Most revenue generating books
    @Query("SELECT oi.book, SUM(oi.subtotal) as totalRevenue FROM OrderItem oi " +
            "JOIN oi.order o WHERE o.status = 'DELIVERED' " +
            "GROUP BY oi.book ORDER BY totalRevenue DESC")
    List<Object[]> findMostPopularBooksByRevenue();

    // Books sold in date range
    @Query("SELECT oi.book, SUM(oi.quantity) FROM OrderItem oi " +
            "JOIN oi.order o WHERE o.status = 'DELIVERED' " +
            "AND o.orderDate BETWEEN :startDate AND :endDate " +
            "GROUP BY oi.book ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findBooksSoldInDateRange(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    // Total quantity sold for a book
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi " +
            "JOIN oi.order o WHERE oi.book.id = :bookId AND o.status = 'DELIVERED'")
    Long getTotalQuantitySoldForBook(@Param("bookId") Long bookId);

    // Books never sold
    @Query("SELECT b FROM Book b WHERE b.id NOT IN " +
            "(SELECT DISTINCT oi.book.id FROM OrderItem oi JOIN oi.order o WHERE o.status = 'DELIVERED')")
    List<Book> findBooksNeverSold();

    // Order items for a specific user
    @Query("SELECT oi FROM OrderItem oi JOIN oi.order o WHERE o.user.id = :userId")
    List<OrderItem> findByUserId(@Param("userId") Long userId);

    // Recently purchased items by user
    @Query("SELECT oi FROM OrderItem oi JOIN oi.order o WHERE o.user.id = :userId " +
            "AND o.status = 'DELIVERED' ORDER BY o.orderDate DESC")
    List<OrderItem> findRecentPurchasesByUser(@Param("userId") Long userId);
}
    