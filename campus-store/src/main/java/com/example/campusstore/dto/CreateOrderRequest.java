package com.example.campusstore.dto;

import java.util.ArrayList;
import java.util.List;

public class CreateOrderRequest {
    private List<OrderItemRequest> items = new ArrayList<>();

    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }
}