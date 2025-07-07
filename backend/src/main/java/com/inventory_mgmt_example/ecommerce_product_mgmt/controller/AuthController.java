package com.inventory_mgmt_example.ecommerce_product_mgmt.controller;

import com.inventory_mgmt_example.ecommerce_product_mgmt.dto.AuthResponse;
import com.inventory_mgmt_example.ecommerce_product_mgmt.dto.LoginRequest;
import com.inventory_mgmt_example.ecommerce_product_mgmt.dto.RegisterRequest;
import com.inventory_mgmt_example.ecommerce_product_mgmt.model.User;
import com.inventory_mgmt_example.ecommerce_product_mgmt.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        AuthResponse response = authService.registerUser(signUpRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<User> getCurrentUser() {
        User currentUser = authService.getCurrentUser();
        if (currentUser != null) {
            // Remove password from response
            currentUser.setPassword(null);
            return ResponseEntity.ok(currentUser);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/logout")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> logoutUser() {
        // JWT is stateless, so logout is handled on client side by removing token
        return ResponseEntity.ok().body("{\"message\": \"User logged out successfully!\"}");
    }
}
