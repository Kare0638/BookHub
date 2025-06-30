// ===== Fixed Book Entity - Resolving JSON Serialization Issues =====
// src/main/java/com/bookhub/entity/Book.java
package com.bookhub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Book title cannot be empty")
    @Size(min = 1, max = 200, message = "Title must be between 1-200 characters")
    private String title;

    @Column(nullable = false)
    @NotBlank(message = "Author cannot be empty")
    @Size(min = 1, max = 100, message = "Author name must be between 1-100 characters")
    private String author;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "ISBN cannot be empty")
    @Pattern(regexp = "^(97[89])?[0-9]{9}[0-9X]$", message = "Invalid ISBN format")
    private String isbn;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "9999.99", message = "Price cannot exceed 9999.99")
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    @NotNull(message = "Stock quantity cannot be null")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(nullable = false)
    private String publisher;

    @Column(name = "page_count")
    @Min(value = 1, message = "Page count must be greater than 0")
    private Integer pageCount;

    @Column(name = "language")
    private String language = "English";

    @Column(name = "weight")
    private Double weight; // in kg

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookStatus status = BookStatus.AVAILABLE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Many-to-One: Multiple books belong to one category
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // One-to-Many: One book can be in multiple order items
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore  // Ignore order items to prevent circular reference
    private Set<OrderItem> orderItems;

    // One-to-Many: One book can be in multiple cart items
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore  // Ignore cart items to prevent circular reference
    private Set<CartItem> cartItems;

    // Constructors
    public Book() {}

    public Book(String title, String author, String isbn, BigDecimal price,
                Integer stockQuantity, String publisher, Category category) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.publisher = publisher;
        this.category = category;
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
    public boolean isAvailable() {
        return status == BookStatus.AVAILABLE && stockQuantity > 0;
    }

    public boolean isInStock() {
        return stockQuantity > 0;
    }

    public void decreaseStock(int quantity) {
        if (stockQuantity >= quantity) {
            stockQuantity -= quantity;
            if (stockQuantity == 0) {
                status = BookStatus.OUT_OF_STOCK;
            }
        } else {
            throw new IllegalArgumentException("Insufficient stock available");
        }
    }

    public void increaseStock(int quantity) {
        stockQuantity += quantity;
        if (status == BookStatus.OUT_OF_STOCK && stockQuantity > 0) {
            status = BookStatus.AVAILABLE;
        }
    }

    public String getDisplayTitle() {
        return title + " by " + author;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public Integer getPageCount() { return pageCount; }
    public void setPageCount(Integer pageCount) { this.pageCount = pageCount; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public BookStatus getStatus() { return status; }
    public void setStatus(BookStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Set<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(Set<OrderItem> orderItems) { this.orderItems = orderItems; }

    public Set<CartItem> getCartItems() { return cartItems; }
    public void setCartItems(Set<CartItem> cartItems) { this.cartItems = cartItems; }
}