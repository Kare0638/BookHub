// ===== 2. BookService =====
// src/main/java/com/bookhub/service/BookService.java
package com.bookhub.service;

import com.bookhub.entity.Book;
import com.bookhub.entity.BookStatus;
import com.bookhub.entity.Category;
import com.bookhub.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // Basic CRUD operations
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    public Book saveBook(Book book) {
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new IllegalArgumentException("Book with this ISBN already exists");
        }
        return bookRepository.save(book);
    }

    public Book updateBook(Long id, Book updatedBook) {
        return bookRepository.findById(id)
                .map(book -> {
                    book.setTitle(updatedBook.getTitle());
                    book.setAuthor(updatedBook.getAuthor());
                    book.setDescription(updatedBook.getDescription());
                    book.setPrice(updatedBook.getPrice());
                    book.setStockQuantity(updatedBook.getStockQuantity());
                    book.setPublisher(updatedBook.getPublisher());
                    book.setPublicationDate(updatedBook.getPublicationDate());
                    book.setPageCount(updatedBook.getPageCount());
                    book.setLanguage(updatedBook.getLanguage());
                    book.setWeight(updatedBook.getWeight());
                    book.setCoverImageUrl(updatedBook.getCoverImageUrl());
                    book.setCategory(updatedBook.getCategory());
                    return bookRepository.save(book);
                })
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    // Search and filter operations
    public List<Book> searchBooks(String keyword) {
        return bookRepository.searchByKeyword(keyword);
    }

    public Page<Book> searchBooks(String keyword, Pageable pageable) {
        return bookRepository.searchByKeyword(keyword, pageable);
    }

    public List<Book> getBooksByCategory(Category category) {
        return bookRepository.findByCategory(category);
    }

    public Page<Book> getBooksByCategory(Category category, Pageable pageable) {
        return bookRepository.findByCategory(category, pageable);
    }

    public List<Book> getBooksByCategoryId(Long categoryId) {
        return bookRepository.findByCategoryId(categoryId);
    }

    public List<Book> getBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }

    public List<Book> getBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Book> getBooksByPublisher(String publisher) {
        return bookRepository.findByPublisherContainingIgnoreCase(publisher);
    }

    // Price range filtering
    public List<Book> getBooksByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return bookRepository.findByPriceBetween(minPrice, maxPrice);
    }

    public Page<Book> getBooksByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return bookRepository.findByPriceBetween(minPrice, maxPrice, pageable);
    }

    // Stock management
    public List<Book> getAvailableBooks() {
        return bookRepository.findByStatusAndStockQuantityGreaterThan(BookStatus.AVAILABLE, 0);
    }

    public List<Book> getLowStockBooks(Integer threshold) {
        return bookRepository.findLowStockBooks(threshold);
    }

    public List<Book> getOutOfStockBooks() {
        return bookRepository.findByStockQuantityEquals(0);
    }

    public void updateStock(Long bookId, Integer newStock) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        book.setStockQuantity(newStock);
        if (newStock > 0 && book.getStatus() == BookStatus.OUT_OF_STOCK) {
            book.setStatus(BookStatus.AVAILABLE);
        } else if (newStock == 0) {
            book.setStatus(BookStatus.OUT_OF_STOCK);
        }

        bookRepository.save(book);
    }

    public void decreaseStock(Long bookId, Integer quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        book.decreaseStock(quantity);
        bookRepository.save(book);
    }

    public void increaseStock(Long bookId, Integer quantity) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        book.increaseStock(quantity);
        bookRepository.save(book);
    }

    // Status management
    public void updateBookStatus(Long bookId, BookStatus status) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        book.setStatus(status);
        bookRepository.save(book);
    }

    // Advanced filtering
    public Page<Book> getBooksWithFilters(Long categoryId, BigDecimal minPrice,
                                          BigDecimal maxPrice, Boolean inStock,
                                          BookStatus status, Pageable pageable) {
        return bookRepository.findBooksWithFilters(categoryId, minPrice, maxPrice, inStock, status, pageable);
    }

    // Featured and popular books
    public List<Book> getFeaturedBooks(Pageable pageable) {
        return bookRepository.findFeaturedBooks(pageable);
    }

    public List<Book> getMostPopularBooks(Pageable pageable) {
        return bookRepository.findMostPopularBooks(pageable);
    }

    public List<Book> getSimilarBooks(Long bookId, Pageable pageable) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        return bookRepository.findSimilarBooks(book.getCategory(), bookId, pageable);
    }

    // Validation
    public boolean isIsbnAvailable(String isbn) {
        return !bookRepository.existsByIsbn(isbn);
    }

    public boolean isBookAvailable(Long bookId) {
        return bookRepository.findById(bookId)
                .map(Book::isAvailable)
                .orElse(false);
    }

    // Statistics
    public long getTotalBookCount() {
        return bookRepository.count();
    }

    public long getAvailableBookCount() {
        return bookRepository.countByStatus(BookStatus.AVAILABLE);
    }

    public BigDecimal getAverageBookPrice() {
        return bookRepository.findAveragePrice();
    }

    public List<Object[]> getBookCountByCategory() {
        return bookRepository.findBookCountByCategory();
    }
}