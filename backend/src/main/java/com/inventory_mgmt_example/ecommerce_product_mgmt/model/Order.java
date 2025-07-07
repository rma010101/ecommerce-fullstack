package com.inventory_mgmt_example.ecommerce_product_mgmt.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    
    @DBRef
    private User user;
    
    private List<OrderItem> items;
    private OrderStatus status;
    private double totalAmount;
    private double shippingCost;
    private double taxAmount;
    private double finalAmount;
    
    private ShippingAddress shippingAddress;
    private PaymentInfo paymentInfo;
    
    private LocalDateTime orderDate;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime deliveredDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private String orderNumber;
    private String trackingNumber;
    private String notes;

    // Constructors
    public Order() {
        this.orderDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    public Order(User user, List<OrderItem> items, ShippingAddress shippingAddress) {
        this();
        this.user = user;
        this.items = items;
        this.shippingAddress = shippingAddress;
        this.orderNumber = generateOrderNumber();
        calculateTotals();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        calculateTotals();
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(double shippingCost) {
        this.shippingCost = shippingCost;
        calculateFinalAmount();
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
        calculateFinalAmount();
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDateTime getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public LocalDateTime getDeliveredDate() {
        return deliveredDate;
    }

    public void setDeliveredDate(LocalDateTime deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Helper methods
    private void calculateTotals() {
        if (items != null) {
            this.totalAmount = items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
            calculateFinalAmount();
        }
    }

    private void calculateFinalAmount() {
        this.finalAmount = this.totalAmount + this.shippingCost + this.taxAmount;
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", status=" + status +
                ", finalAmount=" + finalAmount +
                ", orderDate=" + orderDate +
                '}';
    }
}
