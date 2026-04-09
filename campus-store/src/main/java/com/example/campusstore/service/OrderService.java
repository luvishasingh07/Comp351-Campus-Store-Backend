package com.example.campusstore.service;

import com.example.campusstore.dto.CreateOrderRequest;
import com.example.campusstore.dto.OrderItemRequest;
import com.example.campusstore.entity.*;
import com.example.campusstore.exception.ForbiddenException;
import com.example.campusstore.exception.NotFoundException;
import com.example.campusstore.exception.OutOfStockException;
import com.example.campusstore.exception.ValidationException;
import com.example.campusstore.repository.CustomerOrderRepository;
import com.example.campusstore.repository.ProductRepository;
import com.example.campusstore.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderService {

    private final CustomerOrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(CustomerOrderRepository orderRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CustomerOrder createOrder(Long customerId, CreateOrderRequest request) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found."));

        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new ValidationException("Order must contain items.");
        }

        List<OrderItemRequest> validItems = request.getItems().stream()
                .filter(i -> i.getQty() != null && i.getQty() > 0)
                .toList();

        Set<Long> distinctProductIds = new HashSet<>();
        for (OrderItemRequest item : validItems) {
            if (item.getProductId() == null) {
                throw new ValidationException("Product is required.");
            }
            if (item.getQty() == null || item.getQty() <= 0) {
                throw new ValidationException("Quantity must be greater than 0.");
            }
            distinctProductIds.add(item.getProductId());
        }

        if (distinctProductIds.size() < 2) {
            throw new ValidationException("Order must contain at least 2 different products.");
        }

        CustomerOrder order = new CustomerOrder();
        order.setCustomer(customer);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.NEW);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : validItems) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found."));

            if (!Boolean.TRUE.equals(product.getIsActive())) {
                throw new ValidationException("Inactive product cannot be ordered.");
            }

            if (product.getStockQty() < itemRequest.getQty()) {
                throw new OutOfStockException("Not enough stock for product: " + product.getName());
            }

            product.setStockQty(product.getStockQty() - itemRequest.getQty());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQty(itemRequest.getQty());
            orderItem.setUnitPrice(product.getPrice());

            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQty()));
            total = total.add(lineTotal);

            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        order.setTotal(total);

        return orderRepository.save(order);
    }

    public Page<CustomerOrder> getMyOrders(Long customerId, Pageable pageable) {
        return orderRepository.findByCustomerId(customerId, pageable);
    }

    public CustomerOrder getMyOrder(Long customerId, Long orderId) {
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found."));

        if (!order.getCustomer().getId().equals(customerId)) {
            throw new ForbiddenException("You cannot access another customer's order.");
        }

        return order;
    }

    public Page<CustomerOrder> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Transactional
    public CustomerOrder updateStatus(Long orderId, OrderStatus newStatus) {
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found."));

        if (order.getStatus() == OrderStatus.FULFILLED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new ValidationException("Terminal orders cannot change status.");
        }

        if (order.getStatus() == OrderStatus.NEW &&
                !(newStatus == OrderStatus.FULFILLED || newStatus == OrderStatus.CANCELLED)) {
            throw new ValidationException("Invalid status transition.");
        }

        if (newStatus == OrderStatus.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.setStockQty(product.getStockQty() + item.getQty());
            }
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }
}