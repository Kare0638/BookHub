// ===== 4. Cart Entity =====
// src/main/java/com/bookhub/entity/Cart.java
package com.bookhub.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "total_items")
    private Integer totalItems = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // One-to-Many: One cart can have multiple cart items
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<CartItem> cartItems = new HashSet<>();

    // Constructors
    public Cart() {}

    public Cart(User user) {
        this.user = user;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Business methods
    public void calculateTotals() {
        if (cartItems != null && !cartItems.isEmpty()) {
            totalAmount = cartItems.stream()
                    .map(CartItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalItems = cartItems.stream()
                    .mapToInt(CartItem::getQuantity)
                    .sum();
        } else {
            totalAmount = BigDecimal.ZERO;
            totalItems = 0;
        }
    }

    public void addItem(Book book, Integer quantity) {
        CartItem existingItem = findItemByBook(book);
        if (existingItem != null) {
            existingItem.updateQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem(this, book, quantity, book.getPrice());
            cartItems.add(newItem);
        }
        calculateTotals();
    }

    public void removeItem(Book book) {
        cartItems.removeIf(item -> item.getBook().equals(book));
        calculateTotals();
    }

    public void updateItemQuantity(Book book, Integer newQuantity) {
        CartItem item = findItemByBook(book);
        if (item != null) {
            if (newQuantity <= 0) {
                removeItem(book);
            } else {
                item.updateQuantity(newQuantity);
                calculateTotals();
            }
        }
    }

    public void clearCart() {
        cartItems.clear();
        totalAmount = BigDecimal.ZERO;
        totalItems = 0;
    }

    public boolean isEmpty() {
        return cartItems == null || cartItems.isEmpty();
    }

    private CartItem findItemByBook(Book book) {
        return cartItems.stream()
                .filter(item -> item.getBook().equals(book))
                .findFirst()
                .orElse(null);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Set<CartItem> getCartItems() { return cartItems; }
    public void setCartItems(Set<CartItem> cartItems) { this.cartItems = cartItems; }
}