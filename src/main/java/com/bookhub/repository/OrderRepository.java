// ===== 1. OrderRepository =====
// src/main/java/com/bookhub/repository/OrderRepository.java
package com.bookhub.repository;

import com.bookhub.entity.Order;
import com.bookhub.entity.OrderStatus;
import com.bookhub.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Basic find methods
    Optional<Order> findByOrderNumber(String orderNumber);
    boolean existsByOrderNumber(String orderNumber);

    // Find by user
    List<Order> findByUser(User user);
    List<Order> findByUserId(Long userId);
    Page<Order> findByUser(User user, Pageable pageable);
    Page<Order> findByUserId(Long userId, Pageable pageable);

    // Find by status
    List<Order> findByStatus(OrderStatus status);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    List<Order> findByStatusIn(List<OrderStatus> statuses);

    // Find by user and status
    List<Order> findByUserAndStatus(User user, OrderStatus status);
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);
    Page<Order> findByUserAndStatus(User user, OrderStatus status, Pageable pageable);

    // Date range queries
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    Page<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    List<Order> findByOrderDateAfter(LocalDateTime date);
    List<Order> findByOrderDateBefore(LocalDateTime date);

    // Amount range queries
    List<Order> findByTotalAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    List<Order> findByTotalAmountGreaterThanEqual(BigDecimal amount);
    List<Order> findByTotalAmountLessThanEqual(BigDecimal amount);

    // Recent orders
    @Query("SELECT o FROM Order o WHERE o.orderDate >= :date ORDER BY o.orderDate DESC")
    List<Order> findRecentOrders(@Param("date") LocalDateTime date);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.orderDate >= :date ORDER BY o.orderDate DESC")
    List<Order> findRecentOrdersByUser(@Param("userId") Long userId, @Param("date") LocalDateTime date);

    // Orders requiring attention (pending, confirmed)
    @Query("SELECT o FROM Order o WHERE o.status IN ('PENDING', 'CONFIRMED') ORDER BY o.orderDate ASC")
    List<Order> findOrdersRequiringAttention();

    // Statistics queries
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") OrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate >= :date")
    long countOrdersSince(@Param("date") LocalDateTime date);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'DELIVERED'")
    BigDecimal getTotalRevenue();

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'DELIVERED' AND o.orderDate >= :date")
    BigDecimal getRevenueFromDate(@Param("date") LocalDateTime date);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'DELIVERED' AND o.orderDate BETWEEN :startDate AND :endDate")
    BigDecimal getRevenueBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Top customers
    @Query("SELECT o.user, COUNT(o), SUM(o.totalAmount) FROM Order o WHERE o.status = 'DELIVERED' " +
            "GROUP BY o.user ORDER BY SUM(o.totalAmount) DESC")
    List<Object[]> findTopCustomersByRevenue(Pageable pageable);

    @Query("SELECT o.user, COUNT(o) FROM Order o WHERE o.status = 'DELIVERED' " +
            "GROUP BY o.user ORDER BY COUNT(o) DESC")
    List<Object[]> findTopCustomersByOrderCount(Pageable pageable);

    // Monthly/Daily sales
    @Query("SELECT DATE(o.orderDate), COUNT(o), SUM(o.totalAmount) FROM Order o " +
            "WHERE o.status = 'DELIVERED' AND o.orderDate >= :date " +
            "GROUP BY DATE(o.orderDate) ORDER BY DATE(o.orderDate)")
    List<Object[]> getDailySalesFromDate(@Param("date") LocalDateTime date);

    @Query("SELECT YEAR(o.orderDate), MONTH(o.orderDate), COUNT(o), SUM(o.totalAmount) FROM Order o " +
            "WHERE o.status = 'DELIVERED' " +
            "GROUP BY YEAR(o.orderDate), MONTH(o.orderDate) " +
            "ORDER BY YEAR(o.orderDate), MONTH(o.orderDate)")
    List<Object[]> getMonthlySales();

    // Orders with specific criteria
    @Query("SELECT o FROM Order o WHERE " +
            "(:userId IS NULL OR o.user.id = :userId) AND " +
            "(:status IS NULL OR o.status = :status) AND " +
            "(:startDate IS NULL OR o.orderDate >= :startDate) AND " +
            "(:endDate IS NULL OR o.orderDate <= :endDate) AND " +
            "(:minAmount IS NULL OR o.totalAmount >= :minAmount) AND " +
            "(:maxAmount IS NULL OR o.totalAmount <= :maxAmount)")
    Page<Order> findOrdersWithFilters(@Param("userId") Long userId,
                                      @Param("status") OrderStatus status,
                                      @Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate,
                                      @Param("minAmount") BigDecimal minAmount,
                                      @Param("maxAmount") BigDecimal maxAmount,
                                      Pageable pageable);

    // Orders by user ordered by date
    List<Order> findByUserOrderByOrderDateDesc(User user);
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
}