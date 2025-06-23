// src/main/java/com/bookhub/config/DataInitializer.java
package com.bookhub.config;

import com.bookhub.entity.*;
import com.bookhub.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           BookRepository bookRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已有数据，避免重复初始化
        if (categoryRepository.count() > 0) {
            System.out.println("Data already exists, skipping initialization...");
            return;
        }

        System.out.println("Initializing test data...");

        // 1. 创建用户
        createUsers();

        // 2. 创建分类
        createCategories();

        // 3. 创建图书
        createBooks();

        System.out.println("Test data initialization completed!");
    }

    private void createUsers() {
        // 创建管理员用户
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@bookhub.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFirstName("System");
        admin.setLastName("Administrator");
        admin.setRole(Role.ADMIN);
        admin.setEnabled(true);
        userRepository.save(admin);

        // 创建普通用户
        User user1 = new User();
        user1.setUsername("johndoe");
        user1.setEmail("john.doe@example.com");
        user1.setPassword(passwordEncoder.encode("password123"));
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setRole(Role.USER);
        user1.setEnabled(true);
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("janesmith");
        user2.setEmail("jane.smith@example.com");
        user2.setPassword(passwordEncoder.encode("password123"));
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setRole(Role.USER);
        user2.setEnabled(true);
        userRepository.save(user2);

        System.out.println("Created 3 users (1 admin, 2 regular users)");
    }

    private void createCategories() {
        Category fiction = new Category("Fiction", "Fictional literature including novels and short stories");
        fiction.setActive(true);
        categoryRepository.save(fiction);

        Category technology = new Category("Technology", "Programming, software development, and computer science books");
        technology.setActive(true);
        categoryRepository.save(technology);

        Category business = new Category("Business", "Business, management, and entrepreneurship books");
        business.setActive(true);
        categoryRepository.save(business);

        Category science = new Category("Science", "Scientific research, physics, chemistry, and biology");
        science.setActive(true);
        categoryRepository.save(science);

        Category history = new Category("History", "Historical events, biographies, and cultural studies");
        history.setActive(true);
        categoryRepository.save(history);

        System.out.println("Created 5 categories");
    }

    private void createBooks() {
        // 获取分类
        Category fiction = categoryRepository.findByName("Fiction").orElse(null);
        Category technology = categoryRepository.findByName("Technology").orElse(null);
        Category business = categoryRepository.findByName("Business").orElse(null);
        Category science = categoryRepository.findByName("Science").orElse(null);
        Category history = categoryRepository.findByName("History").orElse(null);

        // Fiction books
        createBook("To Kill a Mockingbird", "Harper Lee", "9780061120084",
                "A gripping tale of racial injustice and childhood innocence in the American South.",
                new BigDecimal("12.99"), 50, "HarperCollins", 376, fiction);

        createBook("1984", "George Orwell", "9780451524935",
                "A dystopian social science fiction novel about totalitarian control.",
                new BigDecimal("13.99"), 75, "Signet Classics", 328, fiction);

        createBook("Pride and Prejudice", "Jane Austen", "9780141439518",
                "A romantic novel about manners, education, and moral development.",
                new BigDecimal("11.99"), 60, "Penguin Classics", 432, fiction);

        // Technology books
        createBook("Clean Code", "Robert C. Martin", "9780132350884",
                "A handbook of agile software craftsmanship with practical advice.",
                new BigDecimal("34.99"), 40, "Prentice Hall", 464, technology);

        createBook("Spring Boot in Action", "Craig Walls", "9781617292545",
                "Comprehensive guide to building applications with Spring Boot.",
                new BigDecimal("39.99"), 30, "Manning Publications", 368, technology);

        createBook("Effective Java", "Joshua Bloch", "9780134685991",
                "Best practices for the Java platform from the creator of Java Collections Framework.",
                new BigDecimal("44.99"), 35, "Addison-Wesley", 416, technology);

        createBook("Design Patterns", "Gang of Four", "9780201633610",
                "Elements of reusable object-oriented software design.",
                new BigDecimal("54.99"), 25, "Addison-Wesley", 395, technology);

        // Business books
        createBook("The Lean Startup", "Eric Ries", "9780307887894",
                "How today's entrepreneurs use continuous innovation to create successful businesses.",
                new BigDecimal("16.99"), 45, "Crown Business", 336, business);

        createBook("Good to Great", "Jim Collins", "9780066620992",
                "Why some companies make the leap and others don't.",
                new BigDecimal("18.99"), 55, "HarperBusiness", 320, business);

        // Science books
        createBook("A Brief History of Time", "Stephen Hawking", "9780553380163",
                "From the Big Bang to black holes, exploring the universe.",
                new BigDecimal("15.99"), 40, "Bantam", 256, science);

        createBook("The Selfish Gene", "Richard Dawkins", "9780198788607",
                "Revolutionary approach to understanding evolution and behavior.",
                new BigDecimal("14.99"), 35, "Oxford University Press", 360, science);

        // History books
        createBook("Sapiens", "Yuval Noah Harari", "9780062316097",
                "A brief history of humankind from Stone Age to modern era.",
                new BigDecimal("16.99"), 65, "Harper", 464, history);

        createBook("The Guns of August", "Barbara Tuchman", "9780345476098",
                "The outbreak of World War I and the first month of the war.",
                new BigDecimal("17.99"), 30, "Ballantine Books", 640, history);

        System.out.println("Created 13 books across all categories");
    }

    private void createBook(String title, String author, String isbn, String description,
                            BigDecimal price, Integer stock, String publisher,
                            Integer pageCount, Category category) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setDescription(description);
        book.setPrice(price);
        book.setStockQuantity(stock);
        book.setPublisher(publisher);
        book.setPageCount(pageCount);
        book.setLanguage("English");
        book.setStatus(BookStatus.AVAILABLE);
        book.setCategory(category);
        book.setPublicationDate(LocalDate.now().minusYears((long)(Math.random() * 10)));

        bookRepository.save(book);
    }
}