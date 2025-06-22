// ===== 2. OrderStatus Enum =====
// src/main/java/com/bookhub/entity/OrderStatus.java
package com.bookhub.entity;

public enum OrderStatus {
    PENDING("Order placed, awaiting confirmation"),
    CONFIRMED("Order confirmed, preparing for shipment"),
    SHIPPED("Order shipped, in transit"),
    DELIVERED("Order delivered successfully"),
    CANCELLED("Order cancelled"),
    RETURNED("Order returned by customer");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this != CANCELLED && this != RETURNED;
    }

    public boolean isCompleted() {
        return this == DELIVERED;
    }
}