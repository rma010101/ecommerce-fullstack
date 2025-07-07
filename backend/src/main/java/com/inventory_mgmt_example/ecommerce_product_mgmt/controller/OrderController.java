package com.inventory_mgmt_example.ecommerce_product_mgmt.controller;

import com.inventory_mgmt_example.ecommerce_product_mgmt.dto.OrderCreateDTO;
import com.inventory_mgmt_example.ecommerce_product_mgmt.model.Order;
import com.inventory_mgmt_example.ecommerce_product_mgmt.model.OrderStatus;
import com.inventory_mgmt_example.ecommerce_product_mgmt.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> createOrder(@RequestBody OrderCreateDTO orderCreateDTO) {
        try {
            String username = getCurrentUsername();
            Order order = orderService.createOrder(username, orderCreateDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            String username = getCurrentUsername();
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<Order> orders = orderService.getUserOrders(username, pageable);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getOrderById(@PathVariable String orderId) {
        try {
            Optional<Order> order = orderService.getOrderById(orderId);
            if (order.isPresent()) {
                String username = getCurrentUsername();
                // Users can only view their own orders, admins can view all
                if (isAdmin() || order.get().getUser().getUsername().equals(username)) {
                    return ResponseEntity.ok(order.get());
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied"));
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/order-number/{orderNumber}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getOrderByOrderNumber(@PathVariable String orderNumber) {
        try {
            Optional<Order> order = orderService.getOrderByOrderNumber(orderNumber);
            if (order.isPresent()) {
                String username = getCurrentUsername();
                // Users can only view their own orders, admins can view all
                if (isAdmin() || order.get().getUser().getUsername().equals(username)) {
                    return ResponseEntity.ok(order.get());
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access denied"));
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> cancelOrder(@PathVariable String orderId) {
        try {
            String username = getCurrentUsername();
            Order order = orderService.cancelOrder(orderId, username);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<?> trackOrder(@PathVariable String trackingNumber) {
        try {
            Optional<Order> order = orderService.findOrderByTrackingNumber(trackingNumber);
            if (order.isPresent()) {
                // Return limited info for public tracking
                Order trackingInfo = order.get();
                return ResponseEntity.ok(Map.of(
                    "orderNumber", trackingInfo.getOrderNumber(),
                    "status", trackingInfo.getStatus(),
                    "orderDate", trackingInfo.getOrderDate(),
                    "estimatedDeliveryDate", trackingInfo.getEstimatedDeliveryDate(),
                    "deliveredDate", trackingInfo.getDeliveredDate(),
                    "trackingNumber", trackingInfo.getTrackingNumber()
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Admin endpoints
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<Order> orders = orderService.getAllOrders(pageable);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getOrdersByStatus(
            @PathVariable OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderDate"));
            Page<Order> orders = orderService.getOrdersByStatus(status, pageable);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/admin/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable String orderId,
            @RequestBody Map<String, String> request) {
        try {
            String statusStr = request.get("status");
            OrderStatus status = OrderStatus.valueOf(statusStr.toUpperCase());
            Order order = orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/admin/{orderId}/tracking")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addTrackingNumber(
            @PathVariable String orderId,
            @RequestBody Map<String, String> request) {
        try {
            String trackingNumber = request.get("trackingNumber");
            Order order = orderService.addTrackingNumber(orderId, trackingNumber);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/recent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getRecentOrders(@RequestParam(defaultValue = "30") int days) {
        try {
            List<Order> orders = orderService.getRecentOrders(days);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getOrderStats() {
        try {
            long totalOrders = orderService.getOrderCount();
            return ResponseEntity.ok(Map.of("totalOrders", totalOrders));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Helper methods
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }
}
