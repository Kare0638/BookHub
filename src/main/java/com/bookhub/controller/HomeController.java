// src/main/java/com/bookhub/controller/HomeController.java
package com.bookhub.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Welcome to BookHub Online Bookstore!";
    }

    @GetMapping("/health")
    public String health() {
        return "Application is running successfully!";
    }
}