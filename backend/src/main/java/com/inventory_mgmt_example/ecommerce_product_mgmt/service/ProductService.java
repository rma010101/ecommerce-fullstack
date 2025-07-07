package com.inventory_mgmt_example.ecommerce_product_mgmt.service;

import com.inventory_mgmt_example.ecommerce_product_mgmt.dto.ProductCreateDTO;
import com.inventory_mgmt_example.ecommerce_product_mgmt.exception.DuplicateProductException;
import com.inventory_mgmt_example.ecommerce_product_mgmt.exception.ProductNotFoundException;
import com.inventory_mgmt_example.ecommerce_product_mgmt.model.Product;
import com.inventory_mgmt_example.ecommerce_product_mgmt.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    /**
     * Get all products
     */
    @Cacheable(value = "products", key = "'all'")
    public List<Product> getAllProducts() {
        logger.debug("Fetching all products");
        return productRepository.findAll();
    }

    /**
     * Get product by ID
     */
    @Cacheable(value = "products", key = "#id")
    public Product getProductById(String id) {
        logger.debug("Fetching product with id: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> ProductNotFoundException.byId(id));
    }

    /**
     * Create a new product
     */
    @CacheEvict(value = "products", key = "'all'")
    public Product createProduct(ProductCreateDTO productDTO) {
        logger.debug("Creating new product: {}", productDTO.getName());
        
        // Check if product name already exists
        if (productRepository.existsByNameIgnoreCase(productDTO.getName())) {
            throw DuplicateProductException.byName(productDTO.getName());
        }

        Product product = convertToEntity(productDTO);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        
        Product savedProduct = productRepository.save(product);
        logger.info("Created product with id: {}", savedProduct.getId());
        return savedProduct;
    }

    /**
     * Create multiple products
     */
    public List<Product> createBulkProducts(List<ProductCreateDTO> productDTOs) {
        logger.debug("Creating {} products in bulk", productDTOs.size());
        
        // Validate that all products have unique names
        for (ProductCreateDTO productDTO : productDTOs) {
            if (productRepository.existsByNameIgnoreCase(productDTO.getName())) {
                throw DuplicateProductException.byName(productDTO.getName());
            }
        }
        
        // Check for duplicate names within the request
        long uniqueNamesCount = productDTOs.stream()
                .map(p -> p.getName().toLowerCase())
                .distinct()
                .count();
        
        if (uniqueNamesCount != productDTOs.size()) {
            throw new DuplicateProductException("Duplicate product names found in the request");
        }
        
        List<Product> products = productDTOs.stream()
                .map(this::convertToEntity)
                .peek(product -> {
                    product.setCreatedAt(LocalDateTime.now());
                    product.setUpdatedAt(LocalDateTime.now());
                })
                .toList();
        
        List<Product> savedProducts = productRepository.saveAll(products);
        logger.info("Created {} products in bulk", savedProducts.size());
        return savedProducts;
    }

    /**
     * Update product
     */
    @CachePut(value = "products", key = "#id")
    @CacheEvict(value = "products", key = "'all'")
    public Product updateProduct(String id, ProductCreateDTO productDTO) {
        logger.debug("Updating product with id: {}", id);
        
        Product existingProduct = getProductById(id);
        
        // Check if new name conflicts with existing products (excluding current one)
        Optional<Product> productWithSameName = productRepository.findByNameIgnoreCase(productDTO.getName());
        if (productWithSameName.isPresent() && !productWithSameName.get().getId().equals(id)) {
            throw DuplicateProductException.byName(productDTO.getName());
        }
        
        updateProductFields(existingProduct, productDTO);
        existingProduct.setUpdatedAt(LocalDateTime.now());
        
        Product updatedProduct = productRepository.save(existingProduct);
        logger.info("Updated product with id: {}", id);
        return updatedProduct;
    }

    /**
     * Delete product
     */
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(String id) {
        logger.debug("Deleting product with id: {}", id);
        
        if (!productRepository.existsById(id)) {
            throw ProductNotFoundException.byId(id);
        }
        
        productRepository.deleteById(id);
        logger.info("Deleted product with id: {}", id);
    }

    /**
     * Update product quantity
     */
    public Product updateProductQuantity(String id, int quantity) {
        logger.debug("Updating quantity for product id: {} to {}", id, quantity);
        
        Product product = getProductById(id);
        
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        
        product.setQuantity(quantity);
        product.setUpdatedAt(LocalDateTime.now());
        
        Product updatedProduct = productRepository.save(product);
        logger.info("Updated quantity for product id: {} to {}", id, quantity);
        return updatedProduct;
    }

    /**
     * Update product price
     */
    public Product updateProductPrice(String id, double price) {
        logger.debug("Updating price for product id: {} to {}", id, price);
        
        Product product = getProductById(id);
        
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        
        product.setPrice(price);
        product.setUpdatedAt(LocalDateTime.now());
        
        Product updatedProduct = productRepository.save(product);
        logger.info("Updated price for product id: {} to {}", id, price);
        return updatedProduct;
    }

    /**
     * Search products by name
     */
    public List<Product> searchProductsByName(String name) {
        logger.debug("Searching products by name: {}", name);
        return productRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Search products by description
     */
    public List<Product> searchProductsByDescription(String description) {
        logger.debug("Searching products by description: {}", description);
        return productRepository.findByDescriptionContainingIgnoreCase(description);
    }

    /**
     * Get products by price range
     */
    public List<Product> getProductsByPriceRange(double minPrice, double maxPrice) {
        logger.debug("Fetching products in price range: {} - {}", minPrice, maxPrice);
        
        if (minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
            throw new IllegalArgumentException("Invalid price range");
        }
        
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    /**
     * Get low stock products
     */
    public List<Product> getLowStockProducts(int threshold) {
        logger.debug("Fetching low stock products with threshold: {}", threshold);
        return productRepository.findByQuantityLessThanEqual(threshold);
    }

    /**
     * Get products in stock
     */
    public List<Product> getInStockProducts() {
        logger.debug("Fetching products in stock");
        return productRepository.findByQuantityGreaterThan(0);
    }

    /**
     * Get products by category
     */
    public List<Product> getProductsByCategory(String category) {
        logger.debug("Fetching products by category: {}", category);
        return productRepository.findByCategory(category);
    }

    /**
     * Get products by brand
     */
    public List<Product> getProductsByBrand(String brand) {
        logger.debug("Fetching products by brand: {}", brand);
        return productRepository.findByBrand(brand);
    }

    /**
     * Update inventory (alias for updateProductQuantity)
     */
    public Product updateInventory(String id, int quantity) {
        return updateProductQuantity(id, quantity);
    }

    /**
     * Convert DTO to Entity
     */
    private Product convertToEntity(ProductCreateDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setSku(dto.getSku());
        product.setCategory(dto.getCategory());
        product.setBrand(dto.getBrand());
        return product;
    }

    /**
     * Update product fields from DTO
     */
    private void updateProductFields(Product product, ProductCreateDTO dto) {
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());
        product.setSku(dto.getSku());
        product.setCategory(dto.getCategory());
        product.setBrand(dto.getBrand());
    }
}
