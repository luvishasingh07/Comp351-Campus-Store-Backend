package com.example.campusstore.dto;

public class OrderItemRequest {
    private Long productId;
    private Integer qty;

    public Long getProductId() { return productId; }
    public Integer getQty() { return qty; }

    public void setProductId(Long productId) { this.productId = productId; }
    public void setQty(Integer qty) { this.qty = qty; }
}