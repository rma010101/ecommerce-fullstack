package com.inventory_mgmt_example.ecommerce_product_mgmt.repository;

import com.inventory_mgmt_example.ecommerce_product_mgmt.model.Order;
import com.inventory_mgmt_example.ecommerce_product_mgmt.model.OrderStatus;
import com.inventory_mgmt_example.ecommerce_product_mgmt.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    
    // Find orders by user
    List<Order> findByUserOrderByOrderDateDesc(User user);
    Page<Order> findByUserOrderByOrderDateDesc(User user, Pageable pageable);
    
    // Find orders by user and status
    List<Order> findByUserAndStatus(User user, OrderStatus status);
    
    // Find orders by status
    List<Order> findByStatus(OrderStatus status);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    
    // Find order by order number
    Optional<Order> findByOrderNumber(String orderNumber);
    
    // Find orders by date range
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find orders by user and date range
    List<Order> findByUserAndOrderDateBetween(User user, LocalDateTime startDate, LocalDateTime endDate);
    
    // Count orders by user
    long countByUser(User user);
    
    // Count orders by status
    long countByStatus(OrderStatus status);
    
    // Find recent orders
    @Query("{'orderDate': {$gte: ?0}}")
    List<Order> findRecentOrders(LocalDateTime since);
    
    // Find orders with total amount greater than
    @Query("{'finalAmount': {$gte: ?0}}")
    List<Order> findOrdersWithAmountGreaterThan(double amount);
    
    // Find orders by tracking number
    Optional<Order> findByTrackingNumber(String trackingNumber);
    
    // Get user's order statistics
    @Query(value = "{'user': ?0}", count = true)
    long countOrdersByUser(User user);
    
    // Find orders that need attention (pending for too long)
    @Query("{'status': 'PENDING', 'orderDate': {$lt: ?0}}")
    List<Order> findStaleOrders(LocalDateTime cutoffDate);
}
