package com.example.campusstore.config;

import com.example.campusstore.entity.*;
import com.example.campusstore.repository.CategoryRepository;
import com.example.campusstore.repository.ProductRepository;
import com.example.campusstore.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(UserRepository userRepository,
                               CategoryRepository categoryRepository,
                               ProductRepository productRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("admin@example.com").isEmpty()) {
                userRepository.save(new User(
                        "Admin",
                        "admin@example.com",
                        passwordEncoder.encode("admin123"),
                        Role.ADMIN
                ));
            }

            if (categoryRepository.count() == 0) {
                Category books = categoryRepository.save(new Category("Books"));
                Category electronics = categoryRepository.save(new Category("Electronics"));

                productRepository.save(new Product("Notebook", "College notebook", new BigDecimal("5.00"), 20, true, books));
                productRepository.save(new Product("Pen", "Blue ink pen", new BigDecimal("2.00"), 50, true, books));
                productRepository.save(new Product("Backpack", "Student backpack", new BigDecimal("35.00"), 10, true, books));
                productRepository.save(new Product("Mouse", "Wireless mouse", new BigDecimal("18.00"), 15, true, electronics));
                productRepository.save(new Product("Keyboard", "USB keyboard", new BigDecimal("25.00"), 12, true, electronics));
                productRepository.save(new Product("Headphones", "Over-ear headphones", new BigDecimal("45.00"), 8, true, electronics));
            }
        };
    }
}