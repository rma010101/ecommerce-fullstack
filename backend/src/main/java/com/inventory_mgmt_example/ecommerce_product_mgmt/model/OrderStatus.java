package com.inventory_mgmt_example.ecommerce_product_mgmt.model;

public enum OrderStatus {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    PROCESSING("Processing"),
    SHIPPED("Shipped"),
    OUT_FOR_DELIVERY("Out for Delivery"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled"),
    RETURNED("Returned"),
    REFUNDED("Refunded");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
