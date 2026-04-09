package com.example.campusstore.controller;

import com.example.campusstore.dto.CategoryForm;
import com.example.campusstore.dto.OrderStatusUpdateRequest;
import com.example.campusstore.dto.ProductForm;
import com.example.campusstore.service.CategoryService;
import com.example.campusstore.service.OrderService;
import com.example.campusstore.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final OrderService orderService;

    public AdminController(CategoryService categoryService, ProductService productService, OrderService orderService) {
        this.categoryService = categoryService;
        this.productService = productService;
        this.orderService = orderService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("categories", categoryService.listAll());
        model.addAttribute("products", productService.listAllAdmin());
        model.addAttribute("orders", orderService.getAllOrders(PageRequest.of(0, 20)));
        model.addAttribute("categoryForm", new CategoryForm());
        model.addAttribute("productForm", new ProductForm());
        model.addAttribute("statusForm", new OrderStatusUpdateRequest());
        return "admin-dashboard";
    }

    @PostMapping("/categories")
    public String createCategory(@Valid @ModelAttribute CategoryForm categoryForm) {
        categoryService.create(categoryForm.getName());
        return "redirect:/admin";
    }

    @PostMapping("/products")
    public String createProduct(@Valid @ModelAttribute ProductForm productForm) {
        productService.create(productForm);
        return "redirect:/admin";
    }

    @PostMapping("/products/{id}/edit")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute ProductForm productForm) {
        productService.update(id, productForm);
        return "redirect:/admin";
    }

    @PostMapping("/products/{id}/deactivate")
    public String deactivateProduct(@PathVariable Long id) {
        productService.deactivate(id);
        return "redirect:/admin";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id,
                                    @ModelAttribute OrderStatusUpdateRequest request) {
        orderService.updateStatus(id, request.getStatus());
        return "redirect:/admin";
    }
}