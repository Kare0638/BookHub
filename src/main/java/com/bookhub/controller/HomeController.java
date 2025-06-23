// src/main/java/com/bookhub/controller/HomeController.java
package com.bookhub.controller;

import com.bookhub.entity.Book;
import com.bookhub.entity.Category;
import com.bookhub.service.BookService;
import com.bookhub.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final BookService bookService;
    private final CategoryService categoryService;

    @Autowired
    public HomeController(BookService bookService, CategoryService categoryService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
    }

    @GetMapping("/")
    public String home(Model model) {
        // 获取分类列表
        List<Category> categories = categoryService.getActiveCategories();
        model.addAttribute("categories", categories);

        // 获取推荐图书（前8本）
        Pageable pageable = PageRequest.of(0, 8);
        List<Book> featuredBooks = bookService.getFeaturedBooks(pageable);
        model.addAttribute("featuredBooks", featuredBooks);

        // 获取最新图书
        List<Book> newArrivals = bookService.getAllBooks(PageRequest.of(0, 4)).getContent();
        model.addAttribute("newArrivals", newArrivals);

        return "index";
    }

    @GetMapping("/books")
    public String books(Model model,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "12") int size,
                        @RequestParam(required = false) Long categoryId,
                        @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size);

        // 获取分类列表用于筛选
        List<Category> categories = categoryService.getActiveCategories();
        model.addAttribute("categories", categories);

        // 根据条件获取图书
        if (search != null && !search.trim().isEmpty()) {
            model.addAttribute("books", bookService.searchBooks(search.trim(), pageable));
            model.addAttribute("currentSearch", search);
        } else if (categoryId != null) {
            model.addAttribute("books", bookService.getBooksByCategoryId(categoryId));
            model.addAttribute("currentCategoryId", categoryId);
        } else {
            model.addAttribute("books", bookService.getAllBooks(pageable));
        }

        return "books";
    }

    @GetMapping("/books/{id}")
    public String bookDetail(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        model.addAttribute("book", book);

        // 获取相似图书
        Pageable pageable = PageRequest.of(0, 4);
        List<Book> similarBooks = bookService.getSimilarBooks(id, pageable);
        model.addAttribute("similarBooks", similarBooks);

        return "book-detail";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}