// ===== 2. Role Enum =====
// src/main/java/com/bookhub/entity/Role.java
package com.bookhub.entity;

public enum Role {
    USER("Regular User"),
    ADMIN("Administrator");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}