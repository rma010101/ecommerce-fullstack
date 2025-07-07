package com.inventory_mgmt_example.ecommerce_product_mgmt.dto;

public class OrderItemDTO {
    private String productId;
    private int quantity;
    private double price;

    // Constructors
    public OrderItemDTO() {}

    public OrderItemDTO(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public OrderItemDTO(String productId, int quantity, double price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and Setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "OrderItemDTO{" +
                "productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
