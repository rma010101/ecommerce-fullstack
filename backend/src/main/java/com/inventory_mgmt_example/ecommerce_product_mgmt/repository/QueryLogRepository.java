package com.inventory_mgmt_example.ecommerce_product_mgmt.repository;

import com.inventory_mgmt_example.ecommerce_product_mgmt.model.QueryLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QueryLogRepository extends MongoRepository<QueryLog, String> {
    
    /**
     * Find query logs by client IP
     */
    List<QueryLog> findByClientIp(String clientIp);
    
    /**
     * Find query logs by HTTP method
     */
    List<QueryLog> findByHttpMethod(String httpMethod);
    
    /**
     * Find query logs by timestamp range
     */
    List<QueryLog> findByTimestampBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Find query logs by success status
     */
    List<QueryLog> findBySuccess(boolean success);
    
    /**
     * Find query logs by response status
     */
    List<QueryLog> findByResponseStatus(int responseStatus);
    
    /**
     * Find query logs with execution time greater than specified milliseconds
     */
    List<QueryLog> findByExecutionTimeMsGreaterThan(long executionTimeMs);
    
    /**
     * Find query logs by request URI containing specified string
     */
    List<QueryLog> findByRequestUriContaining(String uriPart);
    
    /**
     * Find query logs by controller method
     */
    List<QueryLog> findByControllerMethod(String controllerMethod);
    
    /**
     * Find failed requests (success = false)
     */
    List<QueryLog> findBySuccessFalse();
    
    /**
     * Find slow queries (execution time > threshold)
     */
    List<QueryLog> findByExecutionTimeMsGreaterThanOrderByExecutionTimeMsDesc(long threshold);
}
