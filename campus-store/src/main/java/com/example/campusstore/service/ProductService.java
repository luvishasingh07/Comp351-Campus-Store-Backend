package com.example.campusstore.service;

import com.example.campusstore.dto.ProductForm;
import com.example.campusstore.entity.Category;
import com.example.campusstore.entity.Product;
import com.example.campusstore.exception.NotFoundException;
import com.example.campusstore.exception.ValidationException;
import com.example.campusstore.repository.ProductRepository;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductService(ProductRepository productRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    public Page<Product> search(String name, Long categoryId, Boolean inStock,
                                int page, int size, String sortBy, String sortDir) {

        Sort sort = Sort.by(sortBy == null ? "name" : sortBy);
        sort = "desc".equalsIgnoreCase(sortDir) ? sort.descending() : sort.ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isTrue(root.get("isActive")));

            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }

            if (Boolean.TRUE.equals(inStock)) {
                predicates.add(cb.greaterThan(root.get("stockQty"), 0));
            }

            if (Boolean.FALSE.equals(inStock)) {
                predicates.add(cb.equal(root.get("stockQty"), 0));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return productRepository.findAll(spec, pageable);
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found."));
    }

    public Product create(ProductForm form) {
        validateForm(form);
        Category category = categoryService.getById(form.getCategoryId());

        Product product = new Product(
                form.getName(),
                form.getDescription(),
                form.getPrice(),
                form.getStockQty(),
                true,
                category
        );

        return productRepository.save(product);
    }

    public Product update(Long id, ProductForm form) {
        validateForm(form);
        Product product = getById(id);
        Category category = categoryService.getById(form.getCategoryId());

        product.setName(form.getName());
        product.setDescription(form.getDescription());
        product.setPrice(form.getPrice());
        product.setStockQty(form.getStockQty());
        product.setCategory(category);

        return productRepository.save(product);
    }

    public void deactivate(Long id) {
        Product product = getById(id);
        product.setIsActive(false);
        productRepository.save(product);
    }

    public List<Product> listAllAdmin() {
        return productRepository.findAll();
    }

    private void validateForm(ProductForm form) {
        if (form.getPrice() == null || form.getPrice().signum() < 0) {
            throw new ValidationException("Price must be 0 or more.");
        }
        if (form.getStockQty() == null || form.getStockQty() < 0) {
            throw new ValidationException("Stock must be 0 or more.");
        }
    }
}