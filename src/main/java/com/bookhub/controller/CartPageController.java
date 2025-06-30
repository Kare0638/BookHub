// ===== CartPageController - Cart Page Controller =====
// src/main/java/com/bookhub/controller/CartPageController.java
package com.bookhub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for cart-related page routing
 * Handles the display of cart page and other cart-related views
 */
@Controller
public class CartPageController {

    /**
     * Display the shopping cart page
     *
     * @return the cart template name
     */
    @GetMapping("/cart")
    public String cartPage() {
        System.out.println("Cart page accessed"); // Debug log
        return "cart";
    }

    /**
     * Display the checkout page (placeholder for future implementation)
     *
     * @return the checkout template name
     */
    @GetMapping("/checkout")
    public String checkoutPage() {
        // TODO: Add authentication check
        // TODO: Validate cart contents
        return "checkout"; // Will be implemented later
    }

    @GetMapping("/cart-debug")
    public String cartDebugPage() {
        return "cart-debug";
    }
}