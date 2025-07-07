package com.inventory_mgmt_example.ecommerce_product_mgmt.controller;

import com.inventory_mgmt_example.ecommerce_product_mgmt.dto.ProductCreateDTO;
import com.inventory_mgmt_example.ecommerce_product_mgmt.model.Product;
import com.inventory_mgmt_example.ecommerce_product_mgmt.service.ProductService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
@Validated
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * Get all products
     */
    @GetMapping
    @RateLimiter(name = "product-api")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * Get product by ID
     */
    @GetMapping("/{id}")
    @RateLimiter(name = "product-api")
    public ResponseEntity<Product> getProductById(@PathVariable("id") String id) {
        Product product = productService.getProductById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    /**
     * Create a new product
     */
    @PostMapping
    @RateLimiter(name = "product-api")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductCreateDTO productDTO) {
        Product savedProduct = productService.createProduct(productDTO);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    /**
     * Create multiple products at once (bulk creation)
     */
    @PostMapping("/bulk")
    @RateLimiter(name = "bulk-operations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Product>> createBulkProducts(@Valid @RequestBody List<ProductCreateDTO> productDTOs) {
        List<Product> savedProducts = productService.createBulkProducts(productDTOs);
        return new ResponseEntity<>(savedProducts, HttpStatus.CREATED);
    }

    /**
     * Update an existing product
     */
    @PutMapping("/{id}")
    @RateLimiter(name = "product-api")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") String id, @Valid @RequestBody ProductCreateDTO productDTO) {
        Product updatedProduct = productService.updateProduct(id, productDTO);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    /**
     * Delete a product by ID
     */
    @DeleteMapping("/{id}")
    @RateLimiter(name = "product-api")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable("id") String id) {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Search products by name
     */
    @GetMapping("/search")
    @RateLimiter(name = "search-api")
    public ResponseEntity<List<Product>> searchProductsByName(@RequestParam String name) {
        List<Product> products = productService.searchProductsByName(name);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * Get products by price range
     */
    @GetMapping("/price-range")
    @RateLimiter(name = "search-api")
    public ResponseEntity<List<Product>> getProductsByPriceRange(
            @RequestParam @Min(value = 0, message = "Minimum price cannot be negative") double minPrice, 
            @RequestParam @Min(value = 0, message = "Maximum price cannot be negative") double maxPrice) {
        List<Product> products = productService.getProductsByPriceRange(minPrice, maxPrice);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * Get products with low stock
     */
    @GetMapping("/low-stock")
    @RateLimiter(name = "product-api")
    public ResponseEntity<List<Product>> getLowStockProducts(
            @RequestParam(defaultValue = "10") @Min(value = 0, message = "Threshold cannot be negative") int threshold) {
        List<Product> products = productService.getLowStockProducts(threshold);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * Get products by category
     */
    @GetMapping("/category/{category}")
    @RateLimiter(name = "search-api")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable("category") String category) {
        List<Product> products = productService.getProductsByCategory(category);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * Get products by brand
     */
    @GetMapping("/brand/{brand}")
    @RateLimiter(name = "search-api")
    public ResponseEntity<List<Product>> getProductsByBrand(@PathVariable("brand") String brand) {
        List<Product> products = productService.getProductsByBrand(brand);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * Update product inventory
     */
    @PatchMapping("/{id}/inventory")
    @RateLimiter(name = "product-api")
    public ResponseEntity<Product> updateInventory(@PathVariable("id") String id, @RequestParam int quantity) {
        Product updatedProduct = productService.updateInventory(id, quantity);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }
}
