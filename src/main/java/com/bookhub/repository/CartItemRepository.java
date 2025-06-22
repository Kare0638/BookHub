// ===== 4. CartItemRepository =====
// src/main/java/com/bookhub/repository/CartItemRepository.java
package com.bookhub.repository;

import com.bookhub.entity.Book;
import com.bookhub.entity.Cart;
import com.bookhub.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Find by cart
    List<CartItem> findByCart(Cart cart);
    List<CartItem> findByCartId(Long cartId);

    // Find by book
    List<CartItem> findByBook(Book book);
    List<CartItem> findByBookId(Long bookId);

    // Find specific cart item
    Optional<CartItem> findByCartAndBook(Cart cart, Book book);
    Optional<CartItem> findByCartIdAndBookId(Long cartId, Long bookId);

    // Find by user (through cart)
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.user.id = :userId")
    List<CartItem> findByUserId(@Param("userId") Long userId);

    // Check if book is in user's cart
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.user.id = :userId AND ci.book.id = :bookId")
    Optional<CartItem> findByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

    // Most added to cart books
    @Query("SELECT ci.book, COUNT(ci) as addCount FROM CartItem ci " +
            "GROUP BY ci.book ORDER BY addCount DESC")
    List<Object[]> findMostAddedToCartBooks();

    // Cart items created in date range
    List<CartItem> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Items with unavailable books
    @Query("SELECT ci FROM CartItem ci WHERE ci.book.status != 'AVAILABLE' OR ci.book.stockQuantity < ci.quantity")
    List<CartItem> findItemsWithUnavailableBooks();

    // Old cart items (for cleanup)
    @Query("SELECT ci FROM CartItem ci WHERE ci.createdAt < :cutoffDate")
    List<CartItem> findOldCartItems(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Count items in cart
    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.cart.id = :cartId")
    long countByCartId(@Param("cartId") Long cartId);

    // Total quantity in cart
    @Query("SELECT SUM(ci.quantity) FROM CartItem ci WHERE ci.cart.id = :cartId")
    Integer getTotalQuantityInCart(@Param("cartId") Long cartId);

    // Items that need stock check
    @Query("SELECT ci FROM CartItem ci WHERE ci.quantity > ci.book.stockQuantity")
    List<CartItem> findItemsExceedingStock();

    // Recently added items by user
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.user.id = :userId ORDER BY ci.createdAt DESC")
    List<CartItem> findRecentItemsByUser(@Param("userId") Long userId);
}
    