package com.inventory_mgmt_example.ecommerce_product_mgmt.model;

import java.time.LocalDateTime;

public class PaymentInfo {
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String transactionId;
    private String paymentGateway;
    private double amount;
    private String currency;
    private LocalDateTime paymentDate;
    private String paymentReference;
    private String failureReason;

    // Card details (should be encrypted in real implementation)
    private String cardLast4;
    private String cardType;
    private String cardHolderName;

    // Constructors
    public PaymentInfo() {
        this.paymentStatus = PaymentStatus.PENDING;
        this.currency = "USD";
    }

    public PaymentInfo(PaymentMethod paymentMethod, double amount) {
        this();
        this.paymentMethod = paymentMethod;
        this.amount = amount;
    }

    // Getters and Setters
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPaymentGateway() {
        return paymentGateway;
    }

    public void setPaymentGateway(String paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getCardLast4() {
        return cardLast4;
    }

    public void setCardLast4(String cardLast4) {
        this.cardLast4 = cardLast4;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    @Override
    public String toString() {
        return "PaymentInfo{" +
                "paymentMethod=" + paymentMethod +
                ", paymentStatus=" + paymentStatus +
                ", transactionId='" + transactionId + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", paymentDate=" + paymentDate +
                '}';
    }
}
