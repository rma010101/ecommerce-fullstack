package com.inventory_mgmt_example.ecommerce_product_mgmt.model;

public enum PaymentMethod {
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    PAYPAL("PayPal"),
    APPLE_PAY("Apple Pay"),
    GOOGLE_PAY("Google Pay"),
    BANK_TRANSFER("Bank Transfer"),
    CASH_ON_DELIVERY("Cash on Delivery"),
    CRYPTO("Cryptocurrency");

    private final String displayName;

    PaymentMethod(String displayName) {
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
