package com.inventory_mgmt_example.ecommerce_product_mgmt.exception;

public class ProductNotFoundException extends RuntimeException {
    
    public ProductNotFoundException(String message) {
        super(message);
    }
    
    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static ProductNotFoundException byId(String id) {
        return new ProductNotFoundException("Product not found with id: " + id);
    }
    
    public static ProductNotFoundException byName(String name) {
        return new ProductNotFoundException("Product not found with name: " + name);
    }
}
