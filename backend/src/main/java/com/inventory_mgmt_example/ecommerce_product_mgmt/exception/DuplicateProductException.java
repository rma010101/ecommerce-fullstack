package com.inventory_mgmt_example.ecommerce_product_mgmt.exception;

public class DuplicateProductException extends RuntimeException {
    
    public DuplicateProductException(String message) {
        super(message);
    }
    
    public DuplicateProductException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static DuplicateProductException byName(String name) {
        return new DuplicateProductException("Product already exists with name: " + name);
    }
    
    public static DuplicateProductException bySku(String sku) {
        return new DuplicateProductException("Product already exists with SKU: " + sku);
    }
}
