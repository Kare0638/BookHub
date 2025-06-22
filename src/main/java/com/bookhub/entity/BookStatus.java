// ===== 5. BookStatus Enum =====
// src/main/java/com/bookhub/entity/BookStatus.java
package com.bookhub.entity;

public enum BookStatus {
    AVAILABLE("Available for purchase"),
    OUT_OF_STOCK("Currently out of stock"),
    DISCONTINUED("No longer available");

    private final String description;

    BookStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAvailable() {
        return this == AVAILABLE;
    }
}