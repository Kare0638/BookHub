📚 BookHub Online Bookstore
<div align="center">
Show Image
A modern, full-stack online bookstore built with Spring Boot
Show Image
Show Image
Show Image
Show Image
Show Image
🌟 Live Demo • 📖 Documentation • 🚀 Quick Start • 💬 Contact
</div>

🎯 Project Overview
BookHub is a comprehensive online bookstore application that demonstrates modern web development practices with Spring Boot. The project showcases a complete e-commerce solution with user management, book catalog, search functionality, and responsive design.
✨ What Makes This Project Special

🏗️ Enterprise Architecture: Clean separation of concerns with Controller-Service-Repository pattern
🔐 Security First: Complete authentication system with Spring Security integration
📱 Responsive Design: Mobile-friendly interface built with Bootstrap 5
🔍 Smart Search: Advanced search and filtering capabilities
⚡ Performance Optimized: Efficient database queries with pagination
🧪 Well Tested: Comprehensive API testing with real-world scenarios


🚀 Features
🛍️ Customer Features

User Registration & Authentication: Secure account creation with real-time validation
Book Browsing: Paginated book catalog with category filtering
Advanced Search: Search by title, author, or keywords with instant results
Book Details: Comprehensive book information with related recommendations
Responsive Design: Seamless experience across all devices

👨‍💼 Admin Features

User Management: Complete user administration and role management
Book Management: Full CRUD operations for book inventory
Category Management: Organize books with dynamic categories
Dashboard Analytics: Sales and user statistics (planned)

🔧 Technical Features

RESTful API: Complete REST API for all operations
Data Validation: Server-side and client-side validation
Error Handling: Comprehensive error handling and user feedback
Security: Password encryption and role-based access control


🛠️ Technology Stack
Backend
Show Image
Show Image
Show Image
Show Image
Show Image
Database
Show Image
Show Image
Frontend
Show Image
Show Image
Show Image
Show Image

📁 Project Structure
bookhub-online-bookstore/
├── 📁 src/main/java/com/bookhub/
│   ├── 📁 config/          # Security & App Configuration
│   ├── 📁 controller/      # REST & Web Controllers
│   ├── 📁 service/         # Business Logic Layer
│   ├── 📁 repository/      # Data Access Layer
│   ├── 📁 entity/          # JPA Entities
│   └── 📄 BookstoreApplication.java
├── 📁 src/main/resources/
│   ├── 📁 templates/       # Thymeleaf Templates
│   ├── 📁 static/          # CSS, JS, Images
│   └── 📄 application.yml  # Configuration
├── 📁 src/test/           # Unit & Integration Tests
├── 📄 pom.xml             # Maven Dependencies
└── 📄 README.md

🚀 Quick Start
Prerequisites

☕ Java 17+ installed
📦 Maven 3.6+ installed
🗄️ MySQL 8.0+ (optional, H2 included for development)
🔧 Git for version control

🔧 Installation & Setup

Clone the repository
bashgit clone https://github.com/your-username/BookHub.git
cd BookHub

Build the project
bashmvn clean install

Run the application
bashmvn spring-boot:run

Access the application

🌐 Main Application: http://localhost:8080
🗄️ H2 Database Console: http://localhost:8080/h2-console
📚 API Documentation: http://localhost:8080/api/



🔑 Default User Accounts
RoleUsernamePasswordPurposeAdminadminadmin123Full system accessUserjohndoepassword123Regular customerUserjanesmithpassword123Regular customer

📊 Database Schema
<details>
<summary>🗄️ Click to view Entity Relationship Diagram</summary>
┌─────────────┐    ┌──────────────┐    ┌─────────────┐
│    User     │    │   Category   │    │    Book     │
├─────────────┤    ├──────────────┤    ├─────────────┤
│ id (PK)     │    │ id (PK)      │    │ id (PK)     │
│ username    │    │ name         │    │ title       │
│ email       │    │ description  │    │ author      │
│ password    │    │ active       │    │ isbn        │
│ firstName   │    │ created_at   │    │ price       │
│ lastName    │    │ updated_at   │    │ stock       │
│ role        │    └──────────────┘    │ category_id │
│ enabled     │           │             │ created_at  │
│ created_at  │           │             │ updated_at  │
│ updated_at  │           │             └─────────────┘
└─────────────┘           └─────────────────────┘
       │                                       │
       │ 1:N                               N:1 │
       ▼                                       ▼
┌─────────────┐                    ┌─────────────┐
│   Order     │                    │    Cart     │
├─────────────┤                    ├─────────────┤
│ id (PK)     │                    │ id (PK)     │
│ user_id     │                    │ user_id     │
│ order_num   │                    │ total_amt   │
│ total_amt   │                    │ total_items │
│ status      │                    │ created_at  │
│ created_at  │                    │ updated_at  │
└─────────────┘                    └─────────────┘
</details>

🔗 API Documentation
🔐 Authentication Endpoints
httpPOST /api/auth/register    # User registration
POST /api/auth/login       # User login
POST /api/auth/logout      # User logout
📚 Book Management
httpGET    /api/books                    # Get all books (paginated)
GET    /api/books/{id}               # Get book by ID
GET    /api/books/search             # Search books
GET    /api/books/category/{id}      # Get books by category
POST   /api/books                    # Create book (Admin only)
PUT    /api/books/{id}               # Update book (Admin only)
DELETE /api/books/{id}               # Delete book (Admin only)
🏷️ Category Management
httpGET    /api/categories               # Get all categories
GET    /api/categories/{id}          # Get category by ID
POST   /api/categories               # Create category (Admin only)
PUT    /api/categories/{id}          # Update category (Admin only)
DELETE /api/categories/{id}          # Delete category (Admin only)
👥 User Management
httpGET    /api/users                    # Get all users (Admin only)
GET    /api/users/profile            # Get current user profile
PUT    /api/users/profile            # Update user profile
GET    /api/users/check-username     # Check username availability
GET    /api/users/check-email        # Check email availability

🧪 Testing
Run Tests
bash# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report

# Run specific test class
mvn test -Dtest=BookServiceTest
Test Coverage

✅ Unit Tests: Service layer business logic
✅ Integration Tests: Repository layer with database
✅ API Tests: Controller endpoints with MockMvc
🔄 E2E Tests: Complete user workflows (planned)


📸 Screenshots
<details>
<summary>🖼️ Click to view application screenshots</summary>
🏠 Homepage
Professional landing page with featured books and categories
📚 Book Catalog
Responsive book grid with search and filtering capabilities
📖 Book Details
Comprehensive book information with related recommendations
🔐 Authentication
Secure login and registration with real-time validation
👨‍💼 Admin Dashboard
Complete administration interface for managing books and users
</details>

🎯 Key Technical Achievements
🏗️ Architecture & Design Patterns

✅ Layered Architecture: Clear separation between presentation, business, and data layers
✅ Dependency Injection: Loosely coupled components using Spring's IoC container
✅ Repository Pattern: Abstracted data access with Spring Data JPA
✅ MVC Pattern: Clean separation of concerns in web layer

🔐 Security Implementation

✅ Password Encryption: BCrypt hashing for secure password storage
✅ Role-based Access Control: Granular permissions for different user types
✅ CSRF Protection: Built-in protection against cross-site request forgery
✅ Input Validation: Comprehensive server-side and client-side validation

⚡ Performance Optimizations

✅ Pagination: Efficient loading of large datasets
✅ Lazy Loading: Optimized JPA relationships to prevent N+1 problems
✅ Database Indexing: Strategic indexes on frequently queried columns
✅ Query Optimization: Custom JPQL queries for complex operations

🛠️ Code Quality

✅ Clean Code: Meaningful names, small functions, clear comments
✅ SOLID Principles: Well-structured, maintainable codebase
✅ Error Handling: Comprehensive exception handling throughout the application
✅ Logging: Strategic logging for debugging and monitoring


🚀 Future Enhancements
🛒 E-commerce Features

Shopping Cart functionality with persistent storage
Order management with status tracking
Payment integration (Stripe/PayPal)
Email notifications for order updates

🎯 Advanced Features

JWT Token authentication for stateless sessions
Redis caching for improved performance
Elasticsearch integration for advanced search
Real-time notifications with WebSocket

☁️ DevOps & Deployment

Docker containerization
CI/CD pipeline with GitHub Actions
AWS/Azure deployment
Monitoring and logging with ELK stack

📊 Analytics & Reporting

Sales dashboard with charts
User behavior analytics
Inventory management reports
Recommendation engine based on user preferences


🤝 Contributing
Contributions are welcome! Please feel free to submit a Pull Request.
Development Setup

Fork the repository
Create your feature branch (git checkout -b feature/AmazingFeature)
Commit your changes (git commit -m 'Add some AmazingFeature')
Push to the branch (git push origin feature/AmazingFeature)
Open a Pull Request

Code Style

Follow Java naming conventions
Write clear, descriptive commit messages
Add tests for new features
Update documentation as needed


📞 Contact
Developer: Your Name
Email: your.email@example.com
LinkedIn: linkedin.com/in/yourprofile
Portfolio: yourportfolio.com
🌟 Let's Connect!
I'm actively seeking Java Backend Developer opportunities and would love to discuss this project and my technical journey. Feel free to reach out!

📄 License
This project is licensed under the MIT License - see the LICENSE file for details.

🙏 Acknowledgments

Spring Boot Team for the excellent framework
Bootstrap Team for the responsive CSS framework
Font Awesome for the beautiful icons
Stack Overflow Community for invaluable problem-solving insights
GitHub for hosting this project


<div align="center">
⭐ Star this repository if you found it helpful!
Made with ❤️ and lots of ☕
⬆ Back to Top
</div>