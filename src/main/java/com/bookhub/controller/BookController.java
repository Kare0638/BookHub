// src/main/java/com/bookhub/controller/BookController.java
package com.bookhub.controller;

import com.bookhub.entity.Book;
import com.bookhub.entity.BookStatus;
import com.bookhub.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // Get all books with pagination
    @GetMapping
    public ResponseEntity<Page<Book>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Book> books = bookService.getAllBooks(pageable);

        return ResponseEntity.ok(books);
    }

    // Get book by ID
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookService.getBookById(id);
        return book.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Search books
    @GetMapping("/search")
    public ResponseEntity<Page<Book>> searchBooks(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookService.searchBooks(keyword, pageable);

        return ResponseEntity.ok(books);
    }

    // Get books by category
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Book>> getBooksByCategory(@PathVariable Long categoryId) {
        List<Book> books = bookService.getBooksByCategoryId(categoryId);
        return ResponseEntity.ok(books);
    }

    // Filter books with multiple criteria
    @GetMapping("/filter")
    public ResponseEntity<Page<Book>> filterBooks(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) BookStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookService.getBooksWithFilters(
                categoryId, minPrice, maxPrice, inStock, status, pageable);

        return ResponseEntity.ok(books);
    }

    // Get featured books
    @GetMapping("/featured")
    public ResponseEntity<List<Book>> getFeaturedBooks(
            @RequestParam(defaultValue = "8") int limit) {

        Pageable pageable = PageRequest.of(0, limit);
        List<Book> books = bookService.getFeaturedBooks(pageable);

        return ResponseEntity.ok(books);
    }

    // Get popular books
    @GetMapping("/popular")
    public ResponseEntity<List<Book>> getPopularBooks(
            @RequestParam(defaultValue = "8") int limit) {

        Pageable pageable = PageRequest.of(0, limit);
        List<Book> books = bookService.getMostPopularBooks(pageable);

        return ResponseEntity.ok(books);
    }

    // Get similar books
    @GetMapping("/{id}/similar")
    public ResponseEntity<List<Book>> getSimilarBooks(
            @PathVariable Long id,
            @RequestParam(defaultValue = "4") int limit) {

        Pageable pageable = PageRequest.of(0, limit);
        List<Book> books = bookService.getSimilarBooks(id, pageable);

        return ResponseEntity.ok(books);
    }

    // Create new book (Admin only)
    @PostMapping
    public ResponseEntity<Book> createBook(@Valid @RequestBody Book book) {
        try {
            Book savedBook = bookService.saveBook(book);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update book (Admin only)
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @Valid @RequestBody Book book) {
        try {
            Book updatedBook = bookService.updateBook(id, book);
            return ResponseEntity.ok(updatedBook);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Update book stock (Admin only)
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Void> updateBookStock(
            @PathVariable Long id,
            @RequestParam Integer stock) {
        try {
            bookService.updateStock(id, stock);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete book (Admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Check book availability
    @GetMapping("/{id}/availability")
    public ResponseEntity<Boolean> checkBookAvailability(@PathVariable Long id) {
        boolean available = bookService.isBookAvailable(id);
        return ResponseEntity.ok(available);
    }

    // Get book statistics (Admin only)
    @GetMapping("/stats")
    public ResponseEntity<Object> getBookStatistics() {
        return ResponseEntity.ok(new Object() {
            public final long totalBooks = bookService.getTotalBookCount();
            public final long availableBooks = bookService.getAvailableBookCount();
            public final BigDecimal averagePrice = bookService.getAverageBookPrice();
            public final List<Object[]> categoryStats = bookService.getBookCountByCategory();
        });
    }
}