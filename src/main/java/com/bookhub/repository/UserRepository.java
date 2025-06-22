// ===== 1. UserRepository =====
// src/main/java/com/bookhub/repository/UserRepository.java
package com.bookhub.repository;

import com.bookhub.entity.Role;
import com.bookhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Basic find methods
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);

    // Existence check methods
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Find by role
    List<User> findByRole(Role role);
    List<User> findByRoleAndEnabledTrue(Role role);

    // Find enabled/disabled users
    List<User> findByEnabledTrue();
    List<User> findByEnabledFalse();

    // Find users created within time range
    List<User> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Search users by name
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> searchByKeyword(@Param("keyword") String keyword);

    // Count users by role
    long countByRole(Role role);
    long countByEnabledTrue();

    // Find users with orders
    @Query("SELECT DISTINCT u FROM User u JOIN u.orders o")
    List<User> findUsersWithOrders();
}