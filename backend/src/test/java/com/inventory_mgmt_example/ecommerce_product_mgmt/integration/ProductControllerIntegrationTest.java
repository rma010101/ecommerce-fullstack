package com.inventory_mgmt_example.ecommerce_product_mgmt.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventory_mgmt_example.ecommerce_product_mgmt.dto.ProductCreateDTO;
import com.inventory_mgmt_example.ecommerce_product_mgmt.model.Product;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@TestPropertySource(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/ecommerce_test",
    "logging.level.org.springframework.web=DEBUG"
})
class ProductControllerIntegrationTest {

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
    void getAllProducts_ShouldReturnProductList() throws Exception {
        // Given
        Product product = createTestProduct("Test Product", "TEST-001", 99.99);
        productRepository.save(product);

        // When & Then
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test Product")))
                .andExpect(jsonPath("$[0].price", is(99.99)));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void getProductById_WhenProductExists_ShouldReturnProduct() throws Exception {
        // Given
        Product product = createTestProduct("Test Product", "TEST-001", 99.99);
        Product savedProduct = productRepository.save(product);

        // When & Then
        mockMvc.perform(get("/api/products/{id}", savedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.sku", is("TEST-001")))
                .andExpect(jsonPath("$.price", is(99.99)));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void getProductById_WhenProductNotExists_ShouldReturn404() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/products/{id}", "non-existent-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void createProduct_WhenValidData_ShouldCreateProduct() throws Exception {
        // Given
        ProductCreateDTO productDTO = createTestProductDTO("New Product", "NEW-001", 149.99);

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("New Product")))
                .andExpect(jsonPath("$.sku", is("NEW-001")))
                .andExpect(jsonPath("$.price", is(149.99)));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void createProduct_WhenInvalidData_ShouldReturn400() throws Exception {
        // Given - Invalid product with missing required fields
        ProductCreateDTO invalidProductDTO = new ProductCreateDTO();
        invalidProductDTO.setName(""); // Invalid empty name
        invalidProductDTO.setPrice(-10.0); // Invalid negative price

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProductDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void createProduct_WhenDuplicateSku_ShouldReturn409() throws Exception {
        // Given
        Product existingProduct = createTestProduct("Existing Product", "DUPLICATE-001", 99.99);
        productRepository.save(existingProduct);

        ProductCreateDTO duplicateProductDTO = createTestProductDTO("New Product", "DUPLICATE-001", 149.99);

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateProductDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void updateProduct_WhenValidData_ShouldUpdateProduct() throws Exception {
        // Given
        Product product = createTestProduct("Original Product", "ORIG-001", 99.99);
        Product savedProduct = productRepository.save(product);

        ProductCreateDTO updateDTO = createTestProductDTO("Updated Product", "UPDATED-001", 199.99);

        // When & Then
        mockMvc.perform(put("/api/products/{id}", savedProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Updated Product")))
                .andExpect(jsonPath("$.sku", is("UPDATED-001")))
                .andExpect(jsonPath("$.price", is(199.99)));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void deleteProduct_WhenProductExists_ShouldDeleteProduct() throws Exception {
        // Given
        Product product = createTestProduct("Product to Delete", "DELETE-001", 99.99);
        Product savedProduct = productRepository.save(product);

        // When & Then
        mockMvc.perform(delete("/api/products/{id}", savedProduct.getId()))
                .andExpect(status().isNoContent());

        // Verify product is deleted
        mockMvc.perform(get("/api/products/{id}", savedProduct.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void searchProductsByName_ShouldReturnMatchingProducts() throws Exception {
        // Given
        Product product1 = createTestProduct("iPhone 15", "IPHONE-001", 999.99);
        Product product2 = createTestProduct("iPhone 14", "IPHONE-002", 799.99);
        Product product3 = createTestProduct("Samsung Galaxy", "SAMSUNG-001", 899.99);
        
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        // When & Then
        mockMvc.perform(get("/api/products/search")
                .param("name", "iPhone"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("iPhone 15", "iPhone 14")));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void getProductsByPriceRange_ShouldReturnProductsInRange() throws Exception {
        // Given
        Product product1 = createTestProduct("Cheap Product", "CHEAP-001", 50.0);
        Product product2 = createTestProduct("Mid Product", "MID-001", 150.0);
        Product product3 = createTestProduct("Expensive Product", "EXP-001", 500.0);
        
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        // When & Then
        mockMvc.perform(get("/api/products/price-range")
                .param("minPrice", "100")
                .param("maxPrice", "200"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Mid Product")));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void getLowStockProducts_ShouldReturnLowStockProducts() throws Exception {
        // Given
        Product lowStockProduct = createTestProduct("Low Stock Product", "LOW-001", 99.99);
        lowStockProduct.setQuantity(5); // Low stock
        
        Product normalStockProduct = createTestProduct("Normal Stock Product", "NORMAL-001", 99.99);
        normalStockProduct.setQuantity(50); // Normal stock
        
        productRepository.save(lowStockProduct);
        productRepository.save(normalStockProduct);

        // When & Then
        mockMvc.perform(get("/api/products/low-stock")
                .param("threshold", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Low Stock Product")));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void updateInventory_ShouldUpdateProductQuantity() throws Exception {
        // Given
        Product product = createTestProduct("Product for Inventory", "INV-001", 99.99);
        product.setQuantity(50);
        Product savedProduct = productRepository.save(product);

        // When & Then
        mockMvc.perform(patch("/api/products/{id}/inventory", savedProduct.getId())
                .param("quantity", "75"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.quantity", is(75)));
    }

    @Test
    void getAllProducts_WithoutAuthentication_ShouldReturn401() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());
    }

    // Helper methods
    private Product createTestProduct(String name, String sku, double price) {
        Product product = new Product();
        product.setName(name);
        product.setSku(sku);
        product.setDescription("Test description for " + name);
        product.setPrice(price);
        product.setQuantity(100);
        product.setCategory("Electronics");
        product.setBrand("TestBrand");
        return product;
    }

    private ProductCreateDTO createTestProductDTO(String name, String sku, double price) {
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setName(name);
        dto.setSku(sku);
        dto.setDescription("Test description for " + name);
        dto.setPrice(price);
        dto.setQuantity(100);
        dto.setCategory("Electronics");
        dto.setBrand("TestBrand");
        return dto;
    }
}
