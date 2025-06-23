// src/main/java/com/bookhub/controller/UserController.java
package com.bookhub.controller;

import com.bookhub.entity.Role;
import com.bookhub.entity.User;
import com.bookhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Get current user profile
    @GetMapping("/profile")
    public ResponseEntity<User> getCurrentUserProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Optional<User> user = userService.getUserByUsername(username);
            return user.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update current user profile
    @PutMapping("/profile")
    public ResponseEntity<User> updateCurrentUserProfile(@Valid @RequestBody User updatedUser) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Optional<User> currentUserOpt = userService.getUserByUsername(username);
            if (currentUserOpt.isPresent()) {
                User currentUser = currentUserOpt.get();
                User updated = userService.updateUser(currentUser.getId(), updatedUser);
                return ResponseEntity.ok(updated);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Change password
    @PatchMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Optional<User> userOpt = userService.getUserByUsername(username);
            if (userOpt.isPresent()) {
                userService.changePassword(userOpt.get().getId(), oldPassword, newPassword);
                return ResponseEntity.ok("Password changed successfully");
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Admin endpoints

    // Get all users (Admin only)
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Get user by ID (Admin only)
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Search users (Admin only)
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String keyword) {
        List<User> users = userService.searchUsers(keyword);
        return ResponseEntity.ok(users);
    }

    // Get users by role (Admin only)
    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable Role role) {
        List<User> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    // Enable/Disable user (Admin only)
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<String> toggleUserStatus(@PathVariable Long id) {
        try {
            Optional<User> userOpt = userService.getUserById(id);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (user.isEnabled()) {
                    userService.disableUser(id);
                    return ResponseEntity.ok("User disabled successfully");
                } else {
                    userService.enableUser(id);
                    return ResponseEntity.ok("User enabled successfully");
                }
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Promote to admin (Admin only)
    @PatchMapping("/{id}/promote")
    public ResponseEntity<String> promoteToAdmin(@PathVariable Long id) {
        try {
            userService.promoteToAdmin(id);
            return ResponseEntity.ok("User promoted to admin successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Demote to user (Admin only)
    @PatchMapping("/{id}/demote")
    public ResponseEntity<String> demoteToUser(@PathVariable Long id) {
        try {
            userService.demoteToUser(id);
            return ResponseEntity.ok("User demoted to regular user successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Delete user (Admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get user statistics (Admin only)
    @GetMapping("/stats")
    public ResponseEntity<Object> getUserStatistics() {
        return ResponseEntity.ok(new Object() {
            public final long totalUsers = userService.getTotalUserCount();
            public final long activeUsers = userService.getActiveUserCount();
            public final long adminCount = userService.getAdminCount();
        });
    }

    // Check username availability
    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsernameAvailability(@RequestParam String username) {
        boolean available = userService.isUsernameAvailable(username);
        return ResponseEntity.ok(available);
    }

    // Check email availability
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailAvailability(@RequestParam String email) {
        boolean available = userService.isEmailAvailable(email);
        return ResponseEntity.ok(available);
    }
}