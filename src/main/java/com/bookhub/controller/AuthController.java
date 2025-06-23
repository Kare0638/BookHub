// src/main/java/com/bookhub/controller/AuthController.java
package com.bookhub.controller;

import com.bookhub.entity.User;
import com.bookhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // User registration
    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getFirstName(),
                    request.getLastName()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AuthResponse("User registered successfully", user.getId(), user.getUsername()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // User login (basic implementation - JWT will be added later)
    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@Valid @RequestBody LoginRequest request) {
        try {
            Optional<User> userOpt = userService.authenticateUser(request.getUsernameOrEmail(), request.getPassword());

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (!user.isEnabled()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new ErrorResponse("Account is disabled"));
                }

                return ResponseEntity.ok(new AuthResponse("Login successful", user.getId(), user.getUsername()));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Invalid credentials"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Login failed"));
        }
    }

    // Password reset request
    @PostMapping("/forgot-password")
    public ResponseEntity<Object> forgotPassword(@RequestParam String email) {
        try {
            Optional<User> userOpt = userService.getUserByEmail(email);

            if (userOpt.isPresent()) {
                // TODO: Implement email sending logic
                return ResponseEntity.ok(new MessageResponse("Password reset instructions sent to your email"));
            } else {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Email not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Password reset failed"));
        }
    }

    // Logout (for future JWT implementation)
    @PostMapping("/logout")
    public ResponseEntity<Object> logout() {
        // TODO: Implement JWT token invalidation
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }

    // Inner classes for request/response DTOs
    public static class RegisterRequest {
        @jakarta.validation.constraints.NotBlank(message = "Username is required")
        @jakarta.validation.constraints.Size(min = 3, max = 20, message = "Username must be between 3-20 characters")
        private String username;

        @jakarta.validation.constraints.Email(message = "Invalid email format")
        @jakarta.validation.constraints.NotBlank(message = "Email is required")
        private String email;

        @jakarta.validation.constraints.NotBlank(message = "Password is required")
        @jakarta.validation.constraints.Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        private String firstName;
        private String lastName;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
    }

    public static class LoginRequest {
        @jakarta.validation.constraints.NotBlank(message = "Username or email is required")
        private String usernameOrEmail;

        @jakarta.validation.constraints.NotBlank(message = "Password is required")
        private String password;

        // Getters and setters
        public String getUsernameOrEmail() { return usernameOrEmail; }
        public void setUsernameOrEmail(String usernameOrEmail) { this.usernameOrEmail = usernameOrEmail; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class AuthResponse {
        private String message;
        private Long userId;
        private String username;

        public AuthResponse(String message, Long userId, String username) {
            this.message = message;
            this.userId = userId;
            this.username = username;
        }

        public String getMessage() { return message; }
        public Long getUserId() { return userId; }
        public String getUsername() { return username; }
    }

    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() { return error; }
    }

    public static class MessageResponse {
        private String message;

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
    }
}