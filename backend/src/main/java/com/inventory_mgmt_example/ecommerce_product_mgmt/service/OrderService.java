package com.inventory_mgmt_example.ecommerce_product_mgmt.service;

import com.inventory_mgmt_example.ecommerce_product_mgmt.dto.OrderCreateDTO;
import com.inventory_mgmt_example.ecommerce_product_mgmt.dto.OrderItemDTO;
import com.inventory_mgmt_example.ecommerce_product_mgmt.exception.ProductNotFoundException;
import com.inventory_mgmt_example.ecommerce_product_mgmt.model.*;
import com.inventory_mgmt_example.ecommerce_product_mgmt.repository.OrderRepository;
import com.inventory_mgmt_example.ecommerce_product_mgmt.repository.ProductRepository;
import com.inventory_mgmt_example.ecommerce_product_mgmt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // Create a new order
    public Order createOrder(String username, OrderCreateDTO orderCreateDTO) {
        // Find user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Create order items
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemDTO itemDTO : orderCreateDTO.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException("Product not found: " + itemDTO.getProductId()));

            // Check stock availability
            if (product.getQuantity() < itemDTO.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            OrderItem orderItem = new OrderItem(product, itemDTO.getQuantity());
            orderItems.add(orderItem);

            // Update product stock
            product.setQuantity(product.getQuantity() - itemDTO.getQuantity());
            productRepository.save(product);
        }

        // Create order
        Order order = new Order(user, orderItems, orderCreateDTO.getShippingAddress());
        order.setNotes(orderCreateDTO.getNotes());

        // Set payment info
        PaymentInfo paymentInfo = new PaymentInfo(orderCreateDTO.getPaymentMethod(), order.getFinalAmount());
        order.setPaymentInfo(paymentInfo);

        // Calculate shipping and tax (simplified)
        order.setShippingCost(calculateShippingCost(order));
        order.setTaxAmount(calculateTaxAmount(order));

        // Set estimated delivery date
        order.setEstimatedDeliveryDate(LocalDateTime.now().plusDays(7));

        return orderRepository.save(order);
    }

    // Get all orders for a user
    public List<Order> getUserOrders(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    // Get orders with pagination
    public Page<Order> getUserOrders(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return orderRepository.findByUserOrderByOrderDateDesc(user, pageable);
    }

    // Get order by ID
    public Optional<Order> getOrderById(String orderId) {
        return orderRepository.findById(orderId);
    }

    // Get order by order number
    public Optional<Order> getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }

    // Update order status
    public Order updateOrderStatus(String orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setStatus(newStatus);

        // Handle status-specific logic
        switch (newStatus) {
            case PENDING:
                // No special action needed
                break;
            case CONFIRMED:
                // Order confirmed, could send confirmation email
                break;
            case PROCESSING:
                // Order is being processed
                break;
            case SHIPPED:
                if (order.getTrackingNumber() == null) {
                    order.setTrackingNumber(generateTrackingNumber());
                }
                break;
            case OUT_FOR_DELIVERY:
                // Order is out for delivery
                break;
            case DELIVERED:
                order.setDeliveredDate(LocalDateTime.now());
                break;
            case CANCELLED:
                // Restore product stock
                restoreProductStock(order);
                break;
            case RETURNED:
                // Handle return logic
                restoreProductStock(order);
                break;
            case REFUNDED:
                // Handle refund logic
                break;
        }

        return orderRepository.save(order);
    }

    // Cancel order
    public Order cancelOrder(String orderId, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        // Verify user owns the order
        if (!order.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized to cancel this order");
        }

        // Only allow cancellation for certain statuses
        if (order.getStatus() == OrderStatus.DELIVERED || 
            order.getStatus() == OrderStatus.CANCELLED ||
            order.getStatus() == OrderStatus.SHIPPED) {
            throw new RuntimeException("Cannot cancel order in status: " + order.getStatus());
        }

        return updateOrderStatus(orderId, OrderStatus.CANCELLED);
    }

    // Get all orders (admin)
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    // Get orders by status
    public Page<Order> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }

    // Get order statistics
    public long getOrderCount() {
        return orderRepository.count();
    }

    public long getUserOrderCount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return orderRepository.countByUser(user);
    }

    // Helper methods
    private double calculateShippingCost(Order order) {
        // Simplified shipping calculation
        if (order.getTotalAmount() > 50.0) {
            return 0.0; // Free shipping
        }
        return 9.99;
    }

    private double calculateTaxAmount(Order order) {
        // Simplified tax calculation (8% tax)
        return order.getTotalAmount() * 0.08;
    }

    private String generateTrackingNumber() {
        return "TRK-" + System.currentTimeMillis();
    }

    private void restoreProductStock(Order order) {
        for (OrderItem item : order.getItems()) {
            Optional<Product> productOpt = productRepository.findById(item.getProductId());
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                product.setQuantity(product.getQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        }
    }

    // Add tracking number
    public Order addTrackingNumber(String orderId, String trackingNumber) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        
        order.setTrackingNumber(trackingNumber);
        return orderRepository.save(order);
    }

    // Get recent orders
    public List<Order> getRecentOrders(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return orderRepository.findRecentOrders(since);
    }

    // Search orders by tracking number
    public Optional<Order> findOrderByTrackingNumber(String trackingNumber) {
        return orderRepository.findByTrackingNumber(trackingNumber);
    }
}
