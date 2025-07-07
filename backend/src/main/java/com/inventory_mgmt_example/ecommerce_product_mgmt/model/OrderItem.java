package com.inventory_mgmt_example.ecommerce_product_mgmt.model;

import org.springframework.data.mongodb.core.mapping.DBRef;

public class OrderItem {
    @DBRef
    private Product product;
    private String productId;
    private String productName;
    private String productSku;
    private double price;
    private int quantity;
    private double subtotal;

    // Constructors
    public OrderItem() {}

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.productId = product.getId();
        this.productName = product.getName();
        this.productSku = product.getSku();
        this.price = product.getPrice();
        this.quantity = quantity;
        this.subtotal = this.price * this.quantity;
    }

    public OrderItem(String productId, String productName, String productSku, double price, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.productSku = productSku;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = this.price * this.quantity;
    }

    // Getters and Setters
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        if (product != null) {
            this.productId = product.getId();
            this.productName = product.getName();
            this.productSku = product.getSku();
            this.price = product.getPrice();
        }
        calculateSubtotal();
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        calculateSubtotal();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateSubtotal();
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    // Helper methods
    private void calculateSubtotal() {
        this.subtotal = this.price * this.quantity;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", subtotal=" + subtotal +
                '}';
    }
}
