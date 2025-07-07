package com.inventory_mgmt_example.ecommerce_product_mgmt.performance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory_mgmt_example.ecommerce_product_mgmt.dto.ProductCreateDTO;
import com.inventory_mgmt_example.ecommerce_product_mgmt.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.TimeUnit;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@TestPropertySource(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/ecommerce_performance_test",
    "logging.level.org.springframework.cache=DEBUG"
})
class ProductPerformanceTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        
        // Clean up test data
        productRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testBulkProductCreation_Performance() throws Exception {
        int numberOfProducts = 100;
        long startTime = System.currentTimeMillis();

        // Create products in bulk
        for (int i = 0; i < numberOfProducts; i++) {
            ProductCreateDTO productDTO = createTestProductDTO("Product " + i, "SKU-" + i, 99.99 + i);
            
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productDTO)))
                    .andExpect(status().isCreated());
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("Created " + numberOfProducts + " products in " + duration + "ms");
        System.out.println("Average time per product: " + (duration / numberOfProducts) + "ms");
        
        // Assert reasonable performance (adjust threshold as needed)
        assert duration < TimeUnit.SECONDS.toMillis(30) : "Bulk creation took too long: " + duration + "ms";
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testSearchPerformance_WithLargeDataset() throws Exception {
        // Create a larger dataset
        int numberOfProducts = 500;
        
        for (int i = 0; i < numberOfProducts; i++) {
            ProductCreateDTO productDTO = createTestProductDTO(
                "iPhone Product " + i, 
                "IPHONE-" + i, 
                999.99 + (i * 10)
            );
            
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productDTO)))
                    .andExpect(status().isCreated());
        }

        // Test search performance
        int numberOfSearches = 50;
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numberOfSearches; i++) {
            mockMvc.perform(get("/api/products/search")
                    .param("name", "iPhone"))
                    .andExpect(status().isOk());
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("Performed " + numberOfSearches + " searches in " + duration + "ms");
        System.out.println("Average search time: " + (duration / numberOfSearches) + "ms");
        
        // Assert reasonable search performance
        assert (duration / numberOfSearches) < 500 : "Search performance too slow: " + (duration / numberOfSearches) + "ms per search";
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testCachePerformance_RepeatedRequests() throws Exception {
        // Create a product
        ProductCreateDTO productDTO = createTestProductDTO("Cached Product", "CACHE-001", 199.99);
        
        String response = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        // Extract product ID (simplified - in real test, parse JSON)
        String productId = objectMapper.readTree(response).get("id").asText();

        // First request (cache miss)
        long firstRequestStart = System.currentTimeMillis();
        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk());
        long firstRequestTime = System.currentTimeMillis() - firstRequestStart;

        // Subsequent requests (cache hits)
        int numberOfCachedRequests = 10;
        long cachedRequestsStart = System.currentTimeMillis();
        
        for (int i = 0; i < numberOfCachedRequests; i++) {
            mockMvc.perform(get("/api/products/{id}", productId))
                    .andExpect(status().isOk());
        }
        
        long cachedRequestsTime = System.currentTimeMillis() - cachedRequestsStart;
        long averageCachedTime = cachedRequestsTime / numberOfCachedRequests;

        System.out.println("First request (cache miss): " + firstRequestTime + "ms");
        System.out.println("Average cached request: " + averageCachedTime + "ms");
        
        // Cache should improve performance
        assert averageCachedTime <= firstRequestTime : "Cache not improving performance";
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testRateLimitingBehavior() throws Exception {
        // Create a product first
        ProductCreateDTO productDTO = createTestProductDTO("Rate Test Product", "RATE-001", 99.99);
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated());

        // Make many requests to test rate limiting
        int successfulRequests = 0;
        int rateLimitedRequests = 0;
        
        for (int i = 0; i < 120; i++) { // Exceed the 100 requests per minute limit
            try {
                int status = mockMvc.perform(get("/api/products"))
                        .andReturn()
                        .getResponse()
                        .getStatus();
                
                if (status == 200) {
                    successfulRequests++;
                } else if (status == 429) { // Too Many Requests
                    rateLimitedRequests++;
                }
            } catch (Exception e) {
                // Some requests might fail due to rate limiting
                rateLimitedRequests++;
            }
        }

        System.out.println("Successful requests: " + successfulRequests);
        System.out.println("Rate limited requests: " + rateLimitedRequests);
        
        // Should have some rate limited requests when exceeding limits
        assert rateLimitedRequests > 0 : "Rate limiting not working - no requests were limited";
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void testGetAllProducts_ResponseTime() throws Exception {
        // Create some test data
        for (int i = 0; i < 50; i++) {
            ProductCreateDTO productDTO = createTestProductDTO("Product " + i, "SKU-" + i, 99.99 + i);
            mockMvc.perform(post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productDTO)))
                    .andExpect(status().isCreated());
        }

        // Test response time for getting all products
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("Get all products response time: " + duration + "ms");
        
        // Assert reasonable response time (adjust threshold as needed)
        assert duration < 2000 : "Get all products too slow: " + duration + "ms";
    }

    // Helper method
    private ProductCreateDTO createTestProductDTO(String name, String sku, double price) {
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setName(name);
        dto.setSku(sku);
        dto.setDescription("Performance test product: " + name);
        dto.setPrice(price);
        dto.setQuantity(100);
        dto.setCategory("Electronics");
        dto.setBrand("TestBrand");
        return dto;
    }
}
