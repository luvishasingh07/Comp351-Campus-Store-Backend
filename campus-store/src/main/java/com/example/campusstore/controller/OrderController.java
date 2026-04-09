package com.example.campusstore.controller;

import com.example.campusstore.dto.CreateOrderRequest;
import com.example.campusstore.entity.CustomerOrder;
import com.example.campusstore.entity.User;
import com.example.campusstore.repository.UserRepository;
import com.example.campusstore.service.OrderService;
import com.example.campusstore.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final ProductService productService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, ProductService productService, UserRepository userRepository) {
        this.orderService = orderService;
        this.productService = productService;
        this.userRepository = userRepository;
    }

    private User currentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName()).orElseThrow();
    }

    @GetMapping("/orders/create")
    public String createOrderPage(Model model) {
        model.addAttribute("products", productService.listAllAdmin());
        model.addAttribute("createOrderRequest", new CreateOrderRequest());
        return "create-order";
    }

    @PostMapping("/orders")
    public String createOrder(@ModelAttribute CreateOrderRequest createOrderRequest,
                              Authentication authentication,
                              Model model) {
        User user = currentUser(authentication);
        CustomerOrder order = orderService.createOrder(user.getId(), createOrderRequest);
        return "redirect:/my-orders/" + order.getId();
    }

    @GetMapping("/my-orders")
    public String myOrders(Authentication authentication,
                           @RequestParam(defaultValue = "0") int page,
                           Model model) {
        User user = currentUser(authentication);
        model.addAttribute("orders", orderService.getMyOrders(user.getId(), PageRequest.of(page, 5)));
        return "my-orders";
    }

    @GetMapping("/my-orders/{id}")
    public String myOrderDetails(@PathVariable Long id,
                                 Authentication authentication,
                                 Model model) {
        User user = currentUser(authentication);
        model.addAttribute("order", orderService.getMyOrder(user.getId(), id));
        return "order-details";
    }
}