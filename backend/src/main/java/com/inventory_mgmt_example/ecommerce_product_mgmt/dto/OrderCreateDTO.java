package com.inventory_mgmt_example.ecommerce_product_mgmt.dto;

import com.inventory_mgmt_example.ecommerce_product_mgmt.model.ShippingAddress;
import com.inventory_mgmt_example.ecommerce_product_mgmt.model.PaymentMethod;

import java.util.List;

public class OrderCreateDTO {
    private List<OrderItemDTO> items;
    private ShippingAddress shippingAddress;
    private PaymentMethod paymentMethod;
    private String notes;

    // Constructors
    public OrderCreateDTO() {}

    public OrderCreateDTO(List<OrderItemDTO> items, ShippingAddress shippingAddress, PaymentMethod paymentMethod) {
        this.items = items;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "OrderCreateDTO{" +
                "items=" + items +
                ", shippingAddress=" + shippingAddress +
                ", paymentMethod=" + paymentMethod +
                ", notes='" + notes + '\'' +
                '}';
    }
}
