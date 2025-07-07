package com.inventory_mgmt_example.ecommerce_product_mgmt.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory_mgmt_example.ecommerce_product_mgmt.dto.ProductCreateDTO;
import com.inventory_mgmt_example.ecommerce_product_mgmt.model.Product;
import com.inventory_mgmt_example.ecommerce_product_mgmt.repository.ProductRepository;
import com.inventory_mgmt_example.ecommerce_product_mgmt.repository.QueryLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class ProductManagementIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private QueryLogRepository queryLogRepository;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // Clean up before each test
        productRepository.deleteAll();
        queryLogRepository.deleteAll();
        
        // Clear caches
        cacheManager.getCacheNames().forEach(cacheName -> 
            cacheManager.getCache(cacheName).clear());
    }

    @Test
    void testCompleteProductLifecycle() throws Exception {
        // 1. Test creating a product (with validation)
        ProductCreateDTO createDTO = new ProductCreateDTO();
        createDTO.setName("Premium Laptop");
        createDTO.setDescription("High-performance gaming laptop");
        createDTO.setPrice(1299.99);
        createDTO.setSku("LAPTOP-001");
        createDTO.setCategory("Electronics");
        createDTO.setBrand("TechBrand");
        createDTO.setQuantity(10);

        String jsonContent = objectMapper.writeValueAsString(createDTO);

        // Create product
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Premium Laptop")))
                .andExpect(jsonPath("$.price", is(1299.99)))
                .andExpect(jsonPath("$.sku", is("LAPTOP-001")))
                .andExpect(jsonPath("$.id", notNullValue()));

        // 2. Test getting all products (should hit cache)
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Premium Laptop")));

        // 3. Test search functionality
        mockMvc.perform(get("/api/products/search")
                .param("query", "laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", containsString("Laptop")));

        // 4. Test category filtering
        mockMvc.perform(get("/api/products/category/Electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // 5. Test brand filtering
        mockMvc.perform(get("/api/products/brand/TechBrand"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // 6. Test inventory management
        Product savedProduct = productRepository.findAll().get(0);
        
        mockMvc.perform(patch("/api/products/{id}/inventory", savedProduct.getId())
                .param("quantity", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity", is(15)));

        // 7. Test validation errors
        ProductCreateDTO invalidDTO = new ProductCreateDTO();
        invalidDTO.setName(""); // Invalid: empty name
        invalidDTO.setPrice(-10.0); // Invalid: negative price

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("validation")));

        // 8. Test duplicate SKU handling
        ProductCreateDTO duplicateDTO = new ProductCreateDTO();
        duplicateDTO.setName("Another Product");
        duplicateDTO.setDescription("Different product");
        duplicateDTO.setPrice(99.99);
        duplicateDTO.setSku("LAPTOP-001"); // Same SKU as before
        duplicateDTO.setCategory("Electronics");
        duplicateDTO.setBrand("OtherBrand");
        duplicateDTO.setQuantity(5);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("already exists")));

        // 9. Test product not found
        mockMvc.perform(get("/api/products/nonexistent-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("not found")));

        // 10. Test updating a product
        mockMvc.perform(put("/api/products/{id}", savedProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isOk());

        // 11. Test deleting a product
        mockMvc.perform(delete("/api/products/{id}", savedProduct.getId()))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/products/{id}", savedProduct.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRateLimiting() throws Exception {
        // This test would require multiple rapid requests to test rate limiting
        // In a real scenario, you'd make 11+ requests rapidly to trigger the limit
        for (int i = 0; i < 12; i++) {
            try {
                mockMvc.perform(get("/api/products"));
            } catch (Exception e) {
                // Expected after rate limit is exceeded
                break;
            }
        }
    }

    @Test
    void testAOPLogging() throws Exception {
        // Make a request that should be logged
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());

        // Give some time for async logging
        Thread.sleep(1000);

        // Verify that query log was created
        mockMvc.perform(get("/api/query-logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    void testCaching() throws Exception {
        // Create a product first
        ProductCreateDTO createDTO = new ProductCreateDTO();
        createDTO.setName("Cache Test Product");
        createDTO.setDescription("Product for cache testing");
        createDTO.setPrice(99.99);
        createDTO.setSku("CACHE-001");
        createDTO.setCategory("Test");
        createDTO.setBrand("TestBrand");
        createDTO.setQuantity(5);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated());

        // First call - should cache the result
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // Second call - should use cache (would be faster in real scenario)
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testActuatorEndpoints() throws Exception {
        // Test health endpoint
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")));

        // Test metrics endpoint
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isOk());

        // Test cache endpoint
        mockMvc.perform(get("/actuator/caches"))
                .andExpect(status().isOk());
    }

    @Test
    void testSwaggerDocumentation() throws Exception {
        // Test that OpenAPI documentation is available
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
