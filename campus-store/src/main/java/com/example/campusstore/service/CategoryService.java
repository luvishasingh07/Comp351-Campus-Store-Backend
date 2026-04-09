package com.example.campusstore.service;

import com.example.campusstore.entity.Category;
import com.example.campusstore.exception.ValidationException;
import com.example.campusstore.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category create(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Category name is required.");
        }
        return categoryRepository.save(new Category(name.trim()));
    }

    public List<Category> listAll() {
        return categoryRepository.findAll();
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found."));
    }
}