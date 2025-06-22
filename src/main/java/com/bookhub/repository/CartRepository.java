// ===== 3. CartRepository =====
// src/main/java/com/bookhub/repository/CartRepository.java
package com.bookhub.repository;

import com.bookhub.entity.Cart;
import com.bookhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // Find by user
    Optional<Cart> findByUser(User user);
    Optional<Cart> findByUserId(Long userId);

    // Check if user has cart
    boolean existsByUser(User user);
    boolean existsByUserId(Long userId);

    // Find carts with items
    @Query("SELECT c FROM Cart c WHERE c.totalItems > 0")
    List<Cart> findCartsWithItems();

    // Find empty carts
    @Query("SELECT c FROM Cart c WHERE c.totalItems = 0 OR c.totalItems IS NULL")
    List<Cart> findEmptyCarts();

    // Find abandoned carts (not updated for certain time and have items)
    @Query("SELECT c FROM Cart c WHERE c.totalItems > 0 AND c.updatedAt < :cutoffDate")
    List<Cart> findAbandonedCarts(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Find carts created in date range
    List<Cart> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find carts updated recently
    @Query("SELECT c FROM Cart c WHERE c.updatedAt >= :date")
    List<Cart> findRecentlyUpdatedCarts(@Param("date") LocalDateTime date);

    // Statistics
    @Query("SELECT COUNT(c) FROM Cart c WHERE c.totalItems > 0")
    long countActiveCartsWithItems();

    @Query("SELECT AVG(c.totalAmount) FROM Cart c WHERE c.totalItems > 0")
    Double getAverageCartValue();

    @Query("SELECT SUM(c.totalAmount) FROM Cart c WHERE c.totalItems > 0")
    Double getTotalCartValue();
}
