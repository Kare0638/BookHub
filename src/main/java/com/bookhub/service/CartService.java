// ===== CartService - Shopping Cart Service Layer =====
// src/main/java/com/bookhub/service/CartService.java
package com.bookhub.service;

import com.bookhub.entity.Book;
import com.bookhub.entity.Cart;
import com.bookhub.entity.CartItem;
import com.bookhub.entity.User;
import com.bookhub.repository.BookRepository;
import com.bookhub.repository.CartRepository;
import com.bookhub.repository.CartItemRepository;
import com.bookhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Autowired
    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                       BookRepository bookRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    // Get user's cart, create if not exists
    public Cart getOrCreateCartForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart(user);
                    return cartRepository.save(newCart);
                });
    }

    // Get user cart (read-only)
    public Optional<Cart> getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    // Add item to cart
    public CartItem addItemToCart(Long userId, Long bookId, Integer quantity) {
        // Validate book exists and is available
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        if (!book.isAvailable()) {
            throw new IllegalArgumentException("Book is not available");
        }

        if (book.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock available");
        }

        // Get or create cart
        Cart cart = getOrCreateCartForUser(userId);

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndBookId(cart.getId(), bookId);

        CartItem cartItem;
        if (existingItem.isPresent()) {
            // Update quantity
            cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + quantity;

            if (newQuantity > book.getStockQuantity()) {
                throw new IllegalArgumentException("Total quantity exceeds available stock");
            }

            cartItem.setQuantity(newQuantity);
        } else {
            // Create new cart item
            cartItem = new CartItem(cart, book, quantity, book.getPrice());
            cart.getCartItems().add(cartItem);
        }

        cartItem = cartItemRepository.save(cartItem);

        // Recalculate cart totals
        cart.calculateTotals();
        cartRepository.save(cart);

        return cartItem;
    }

    // Update cart item quantity
    public CartItem updateCartItemQuantity(Long userId, Long bookId, Integer newQuantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        CartItem cartItem = cartItemRepository.findByCartIdAndBookId(cart.getId(), bookId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found in cart"));

        Book book = cartItem.getBook();

        if (newQuantity <= 0) {
            // Remove item
            return removeItemFromCart(userId, bookId);
        }

        if (newQuantity > book.getStockQuantity()) {
            throw new IllegalArgumentException("Quantity exceeds available stock");
        }

        cartItem.setQuantity(newQuantity);
        cartItem = cartItemRepository.save(cartItem);

        // Recalculate cart totals
        cart.calculateTotals();
        cartRepository.save(cart);

        return cartItem;
    }

    // Remove item from cart
    public CartItem removeItemFromCart(Long userId, Long bookId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        CartItem cartItem = cartItemRepository.findByCartIdAndBookId(cart.getId(), bookId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found in cart"));

        cartItemRepository.delete(cartItem);
        cart.getCartItems().remove(cartItem);

        // Recalculate cart totals
        cart.calculateTotals();
        cartRepository.save(cart);

        return cartItem;
    }

    // Clear cart
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        cartItemRepository.deleteAll(cart.getCartItems());
        cart.clearCart();
        cartRepository.save(cart);
    }

    // Get cart item count
    public int getCartItemCount(Long userId) {
        return cartRepository.findByUserId(userId)
                .map(Cart::getTotalItems)
                .orElse(0);
    }

    // Validate stock for all items in cart
    public boolean validateCartStock(Long userId) {
        Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
        if (cartOpt.isEmpty()) {
            return true; // Empty cart is always valid
        }

        Cart cart = cartOpt.get();
        return cart.getCartItems().stream()
                .allMatch(item -> item.getBook().getStockQuantity() >= item.getQuantity()
                        && item.getBook().isAvailable());
    }

    // Update unavailable items in cart
    public void updateUnavailableItems(Long userId) {
        Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
        if (cartOpt.isEmpty()) {
            return;
        }

        Cart cart = cartOpt.get();
        boolean updated = false;

        for (CartItem item : cart.getCartItems()) {
            Book book = item.getBook();

            // If item is unavailable, remove it
            if (!book.isAvailable()) {
                cartItemRepository.delete(item);
                updated = true;
                continue;
            }

            // If quantity exceeds stock, adjust quantity
            if (item.getQuantity() > book.getStockQuantity()) {
                if (book.getStockQuantity() > 0) {
                    item.setQuantity(book.getStockQuantity());
                    cartItemRepository.save(item);
                } else {
                    cartItemRepository.delete(item);
                }
                updated = true;
            }
        }

        if (updated) {
            // Reload cart and recalculate totals
            cart = cartRepository.findById(cart.getId()).orElse(cart);
            cart.calculateTotals();
            cartRepository.save(cart);
        }
    }

    // Merge guest cart to user cart (used during login)
    public void mergeGuestCartToUserCart(Long userId, Cart guestCart) {
        if (guestCart == null || guestCart.getCartItems().isEmpty()) {
            return;
        }

        Cart userCart = getOrCreateCartForUser(userId);

        for (CartItem guestItem : guestCart.getCartItems()) {
            try {
                addItemToCart(userId, guestItem.getBook().getId(), guestItem.getQuantity());
            } catch (IllegalArgumentException e) {
                // Ignore items that cannot be added (e.g., insufficient stock)
                System.out.println("Failed to merge item: " + e.getMessage());
            }
        }
    }
}