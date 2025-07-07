package com.inventory_mgmt_example.ecommerce_product_mgmt.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "categories")
public class Category {
    
    @Id
    private String id;
    
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    @Indexed(unique = true)
    private String name;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @NotBlank(message = "Category code is required")
    @Size(min = 2, max = 20, message = "Category code must be between 2 and 20 characters")
    @Indexed(unique = true)
    private String code; // e.g., "ELEC", "CLOTH", "BOOKS"
    
    private String parentCategoryId; // For hierarchical categories
    
    private List<String> subcategoryIds; // Child categories
    
    private boolean active = true;
    
    private String imageUrl;
    
    private int sortOrder = 0; // For display ordering
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    // Constructors
    public Category() {}
    
    public Category(String name, String code, String description) {
        this.name = name;
        this.code = code;
        this.description = description;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getParentCategoryId() {
        return parentCategoryId;
    }
    
    public void setParentCategoryId(String parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }
    
    public List<String> getSubcategoryIds() {
        return subcategoryIds;
    }
    
    public void setSubcategoryIds(List<String> subcategoryIds) {
        this.subcategoryIds = subcategoryIds;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public int getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Helper methods
    public boolean hasParent() {
        return parentCategoryId != null && !parentCategoryId.isEmpty();
    }
    
    public boolean hasSubcategories() {
        return subcategoryIds != null && !subcategoryIds.isEmpty();
    }
    
    @Override
    public String toString() {
        return "Category{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", active=" + active +
                ", parentCategoryId='" + parentCategoryId + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
