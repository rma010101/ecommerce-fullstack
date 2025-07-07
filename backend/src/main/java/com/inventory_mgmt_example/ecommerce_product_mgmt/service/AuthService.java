package com.inventory_mgmt_example.ecommerce_product_mgmt.service;

import com.inventory_mgmt_example.ecommerce_product_mgmt.dto.AuthResponse;
import com.inventory_mgmt_example.ecommerce_product_mgmt.dto.LoginRequest;
import com.inventory_mgmt_example.ecommerce_product_mgmt.dto.RegisterRequest;
import com.inventory_mgmt_example.ecommerce_product_mgmt.exception.DuplicateProductException;
import com.inventory_mgmt_example.ecommerce_product_mgmt.model.Role;
import com.inventory_mgmt_example.ecommerce_product_mgmt.model.User;
import com.inventory_mgmt_example.ecommerce_product_mgmt.repository.UserRepository;
import com.inventory_mgmt_example.ecommerce_product_mgmt.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User userDetails = (User) authentication.getPrincipal();

        return new AuthResponse(jwt, userDetails.getUsername(), userDetails.getEmail(), 
                              userDetails.getFullName(), userDetails.getRole());
    }

    public AuthResponse registerUser(RegisterRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new DuplicateProductException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new DuplicateProductException("Error: Email is already in use!");
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                           signUpRequest.getEmail(),
                           encoder.encode(signUpRequest.getPassword()),
                           signUpRequest.getFirstName(),
                           signUpRequest.getLastName());

        // Set role from request, default to USER if not specified
        user.setRole(signUpRequest.getRole() != null ? signUpRequest.getRole() : Role.USER);

        User savedUser = userRepository.save(user);

        // Generate JWT token for the new user
        String jwt = jwtUtils.generateTokenFromUsername(savedUser.getUsername());

        return new AuthResponse(jwt, savedUser.getUsername(), savedUser.getEmail(), 
                              savedUser.getFullName(), savedUser.getRole());
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }
}
