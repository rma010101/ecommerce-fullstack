package com.inventory_mgmt_example.ecommerce_product_mgmt.service;

import com.inventory_mgmt_example.ecommerce_product_mgmt.dto.ProductCreateDTO;
import com.inventory_mgmt_example.ecommerce_product_mgmt.exception.DuplicateProductException;
import com.inventory_mgmt_example.ecommerce_product_mgmt.exception.ProductNotFoundException;
import com.inventory_mgmt_example.ecommerce_product_mgmt.model.Product;
import com.inventory_mgmt_example.ecommerce_product_mgmt.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductCreateDTO testProductDTO;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId("test-id");
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(99.99);
        testProduct.setQuantity(50);
        testProduct.setSku("TEST-001");
        testProduct.setCategory("Electronics");
        testProduct.setBrand("TestBrand");

        testProductDTO = new ProductCreateDTO();
        testProductDTO.setName("Test Product");
        testProductDTO.setDescription("Test Description");
        testProductDTO.setPrice(99.99);
        testProductDTO.setQuantity(50);
        testProductDTO.setSku("TEST-001");
        testProductDTO.setCategory("Electronics");
        testProductDTO.setBrand("TestBrand");
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        // Given
        List<Product> expectedProducts = Arrays.asList(testProduct);
        when(productRepository.findAll()).thenReturn(expectedProducts);

        // When
        List<Product> actualProducts = productService.getAllProducts();

        // Then
        assertEquals(expectedProducts, actualProducts);
        verify(productRepository).findAll();
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        // Given
        when(productRepository.findById("test-id")).thenReturn(Optional.of(testProduct));

        // When
        Product actualProduct = productService.getProductById("test-id");

        // Then
        assertEquals(testProduct, actualProduct);
        verify(productRepository).findById("test-id");
    }

    @Test
    void getProductById_WhenProductNotExists_ShouldThrowException() {
        // Given
        when(productRepository.findById("non-existent-id")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ProductNotFoundException.class, 
            () -> productService.getProductById("non-existent-id"));
        verify(productRepository).findById("non-existent-id");
    }

    @Test
    void createProduct_WhenValidProduct_ShouldCreateProduct() {
        // Given
        when(productRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
        when(productRepository.existsBySku(anyString())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        Product createdProduct = productService.createProduct(testProductDTO);

        // Then
        assertEquals(testProduct, createdProduct);
        verify(productRepository).existsByNameIgnoreCase("Test Product");
        verify(productRepository).existsBySku("TEST-001");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_WhenDuplicateName_ShouldThrowException() {
        // Given
        when(productRepository.existsByNameIgnoreCase("Test Product")).thenReturn(true);

        // When & Then
        assertThrows(DuplicateProductException.class, 
            () -> productService.createProduct(testProductDTO));
        verify(productRepository).existsByNameIgnoreCase("Test Product");
        verify(productRepository, never()).save(any());
    }

    @Test
    void createProduct_WhenDuplicateSku_ShouldThrowException() {
        // Given
        when(productRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
        when(productRepository.existsBySku("TEST-001")).thenReturn(true);

        // When & Then
        assertThrows(DuplicateProductException.class, 
            () -> productService.createProduct(testProductDTO));
        verify(productRepository).existsBySku("TEST-001");
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_WhenProductExists_ShouldUpdateProduct() {
        // Given
        when(productRepository.findById("test-id")).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        Product updatedProduct = productService.updateProduct("test-id", testProductDTO);

        // Then
        assertEquals(testProduct, updatedProduct);
        verify(productRepository).findById("test-id");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void deleteProduct_WhenProductExists_ShouldDeleteProduct() {
        // Given
        when(productRepository.existsById("test-id")).thenReturn(true);

        // When
        productService.deleteProduct("test-id");

        // Then
        verify(productRepository).existsById("test-id");
        verify(productRepository).deleteById("test-id");
    }

    @Test
    void deleteProduct_WhenProductNotExists_ShouldThrowException() {
        // Given
        when(productRepository.existsById("non-existent-id")).thenReturn(false);

        // When & Then
        assertThrows(ProductNotFoundException.class, 
            () -> productService.deleteProduct("non-existent-id"));
        verify(productRepository).existsById("non-existent-id");
        verify(productRepository, never()).deleteById(anyString());
    }

    @Test
    void searchProductsByName_ShouldReturnMatchingProducts() {
        // Given
        List<Product> expectedProducts = Arrays.asList(testProduct);
        when(productRepository.findByNameContainingIgnoreCase("Test")).thenReturn(expectedProducts);

        // When
        List<Product> actualProducts = productService.searchProductsByName("Test");

        // Then
        assertEquals(expectedProducts, actualProducts);
        verify(productRepository).findByNameContainingIgnoreCase("Test");
    }

    @Test
    void getProductsByPriceRange_ShouldReturnProductsInRange() {
        // Given
        List<Product> expectedProducts = Arrays.asList(testProduct);
        when(productRepository.findByPriceBetween(50.0, 150.0)).thenReturn(expectedProducts);

        // When
        List<Product> actualProducts = productService.getProductsByPriceRange(50.0, 150.0);

        // Then
        assertEquals(expectedProducts, actualProducts);
        verify(productRepository).findByPriceBetween(50.0, 150.0);
    }

    @Test
    void getLowStockProducts_ShouldReturnLowStockProducts() {
        // Given
        List<Product> expectedProducts = Arrays.asList(testProduct);
        when(productRepository.findByQuantityLessThanEqual(10)).thenReturn(expectedProducts);

        // When
        List<Product> actualProducts = productService.getLowStockProducts(10);

        // Then
        assertEquals(expectedProducts, actualProducts);
        verify(productRepository).findByQuantityLessThanEqual(10);
    }

    @Test
    void updateInventory_WhenProductExists_ShouldUpdateQuantity() {
        // Given
        when(productRepository.findById("test-id")).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        Product updatedProduct = productService.updateInventory("test-id", 75);

        // Then
        assertEquals(75, updatedProduct.getQuantity());
        verify(productRepository).findById("test-id");
        verify(productRepository).save(testProduct);
    }
}
