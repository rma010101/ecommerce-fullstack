package com.inventory_mgmt_example.ecommerce_product_mgmt.dto;

import com.inventory_mgmt_example.ecommerce_product_mgmt.model.Role;

public class AuthResponse {
    
    private String token;
    private String type = "Bearer";
    private String username;
    private String email;
    private String fullName;
    private Role role;

    // Constructors
    public AuthResponse() {}

    public AuthResponse(String token, String username, String email, String fullName, Role role) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
