// ===== CartController - Shopping Cart API Controller =====
// src/main/java/com/bookhub/controller/CartController.java
package com.bookhub.controller;

import com.bookhub.entity.Cart;
import com.bookhub.entity.CartItem;
import com.bookhub.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Get user cart
    @GetMapping("/user/{userId}")
    public ResponseEntity<Cart> getUserCart(@PathVariable Long userId) {
        try {
            Cart cart = cartService.getOrCreateCartForUser(userId);
            // Update unavailable items
            cartService.updateUnavailableItems(userId);
            // Reload updated cart
            cart = cartService.getOrCreateCartForUser(userId);
            return ResponseEntity.ok(cart);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Add item to cart
    @PostMapping("/add")
    public ResponseEntity<Object> addItemToCart(@Valid @RequestBody AddToCartRequest request) {
        try {
            CartItem cartItem = cartService.addItemToCart(
                    request.getUserId(),
                    request.getBookId(),
                    request.getQuantity()
            );

            return ResponseEntity.ok(new CartResponse(
                    "Item added to cart successfully",
                    cartItem,
                    cartService.getCartItemCount(request.getUserId())
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // 更新购物车商品数量
    @PutMapping("/update")
    public ResponseEntity<Object> updateCartItem(@Valid @RequestBody UpdateCartItemRequest request) {
        try {
            CartItem cartItem = cartService.updateCartItemQuantity(
                    request.getUserId(),
                    request.getBookId(),
                    request.getQuantity()
            );

            return ResponseEntity.ok(new CartResponse(
                    "Cart item updated successfully",
                    cartItem,
                    cartService.getCartItemCount(request.getUserId())
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // Remove item from cart
    @DeleteMapping("/remove")
    public ResponseEntity<Object> removeItemFromCart(
            @RequestParam Long userId,
            @RequestParam Long bookId) {
        try {
            cartService.removeItemFromCart(userId, bookId);

            return ResponseEntity.ok(new MessageResponse(
                    "Item removed from cart successfully"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // Clear cart
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<Object> clearCart(@PathVariable Long userId) {
        try {
            cartService.clearCart(userId);
            return ResponseEntity.ok(new MessageResponse("Cart cleared successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // Get cart item count
    @GetMapping("/count/{userId}")
    public ResponseEntity<Integer> getCartItemCount(@PathVariable Long userId) {
        int count = cartService.getCartItemCount(userId);
        return ResponseEntity.ok(count);
    }

    // Validate cart stock
    @GetMapping("/validate/{userId}")
    public ResponseEntity<Boolean> validateCartStock(@PathVariable Long userId) {
        boolean valid = cartService.validateCartStock(userId);
        return ResponseEntity.ok(valid);
    }

    // Update unavailable items in cart
    @PostMapping("/update-availability/{userId}")
    public ResponseEntity<Object> updateCartAvailability(@PathVariable Long userId) {
        try {
            cartService.updateUnavailableItems(userId);
            return ResponseEntity.ok(new MessageResponse("Cart availability updated"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to update cart availability"));
        }
    }

    // ===== DTO Classes =====

    // Add to cart request
    public static class AddToCartRequest {
        @NotNull(message = "User ID is required")
        private Long userId;

        @NotNull(message = "Book ID is required")
        private Long bookId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;

        // Constructors
        public AddToCartRequest() {}

        public AddToCartRequest(Long userId, Long bookId, Integer quantity) {
            this.userId = userId;
            this.bookId = bookId;
            this.quantity = quantity;
        }

        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    // Update cart item request
    public static class UpdateCartItemRequest {
        @NotNull(message = "User ID is required")
        private Long userId;

        @NotNull(message = "Book ID is required")
        private Long bookId;

        @NotNull(message = "Quantity is required")
        @Min(value = 0, message = "Quantity cannot be negative")
        private Integer quantity;

        // Constructors
        public UpdateCartItemRequest() {}

        public UpdateCartItemRequest(Long userId, Long bookId, Integer quantity) {
            this.userId = userId;
            this.bookId = bookId;
            this.quantity = quantity;
        }

        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    // Cart response
    public static class CartResponse {
        private String message;
        private CartItem item;
        private Integer totalItems;

        public CartResponse(String message, CartItem item, Integer totalItems) {
            this.message = message;
            this.item = item;
            this.totalItems = totalItems;
        }

        // Getters
        public String getMessage() { return message; }
        public CartItem getItem() { return item; }
        public Integer getTotalItems() { return totalItems; }
    }

    // Error response
    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() { return error; }
    }

    // Message response
    public static class MessageResponse {
        private String message;

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
    }
}
