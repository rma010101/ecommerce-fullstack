package com.inventory_mgmt_example.ecommerce_product_mgmt.repository;

import com.inventory_mgmt_example.ecommerce_product_mgmt.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    
    // Custom query methods for business logic
    
    /**
     * Find products by name containing the given string (case-insensitive)
     */
    List<Product> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find products by price range
     */
    List<Product> findByPriceBetween(double minPrice, double maxPrice);
    
    /**
     * Find products with quantity greater than specified amount
     */
    List<Product> findByQuantityGreaterThan(int quantity);
    
    /**
     * Find products with quantity less than or equal to specified amount (for low stock)
     */
    List<Product> findByQuantityLessThanEqual(int quantity);
    
    /**
     * Find products by exact name (case-insensitive)
     */
    Optional<Product> findByNameIgnoreCase(String name);
    
    /**
     * Find products by description containing the given string (case-insensitive)
     */
    List<Product> findByDescriptionContainingIgnoreCase(String description);
    
    /**
     * Check if a product exists by name (case-insensitive)
     */
    boolean existsByNameIgnoreCase(String name);
    
    /**
     * Find products by category
     */
    List<Product> findByCategory(String category);
    
    /**
     * Find products by brand
     */
    List<Product> findByBrand(String brand);
    
    /**
     * Find products by SKU
     */
    Optional<Product> findBySku(String sku);
    
    /**
     * Check if a product exists by SKU
     */
    boolean existsBySku(String sku);
}
