package com.inventory_mgmt_example.ecommerce_product_mgmt.controller;

import com.inventory_mgmt_example.ecommerce_product_mgmt.model.QueryLog;
import com.inventory_mgmt_example.ecommerce_product_mgmt.repository.QueryLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/query-logs")
@CrossOrigin(origins = "*")
public class QueryLogController {

    @Autowired
    private QueryLogRepository queryLogRepository;

    /**
     * Get all query logs
     */
    @GetMapping
    public ResponseEntity<List<QueryLog>> getAllQueryLogs() {
        try {
            List<QueryLog> logs = queryLogRepository.findAll();
            return new ResponseEntity<>(logs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get query logs by client IP
     */
    @GetMapping("/by-ip/{clientIp}")
    public ResponseEntity<List<QueryLog>> getQueryLogsByClientIp(@PathVariable String clientIp) {
        try {
            List<QueryLog> logs = queryLogRepository.findByClientIp(clientIp);
            return new ResponseEntity<>(logs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get query logs by date range
     */
    @GetMapping("/by-date-range")
    public ResponseEntity<List<QueryLog>> getQueryLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            List<QueryLog> logs = queryLogRepository.findByTimestampBetween(startTime, endTime);
            return new ResponseEntity<>(logs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get failed query logs
     */
    @GetMapping("/failed")
    public ResponseEntity<List<QueryLog>> getFailedQueryLogs() {
        try {
            List<QueryLog> logs = queryLogRepository.findBySuccessFalse();
            return new ResponseEntity<>(logs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get slow query logs (execution time > threshold)
     */
    @GetMapping("/slow")
    public ResponseEntity<List<QueryLog>> getSlowQueryLogs(@RequestParam(defaultValue = "1000") long thresholdMs) {
        try {
            List<QueryLog> logs = queryLogRepository.findByExecutionTimeMsGreaterThanOrderByExecutionTimeMsDesc(thresholdMs);
            return new ResponseEntity<>(logs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get query logs by HTTP method
     */
    @GetMapping("/by-method/{httpMethod}")
    public ResponseEntity<List<QueryLog>> getQueryLogsByHttpMethod(@PathVariable String httpMethod) {
        try {
            List<QueryLog> logs = queryLogRepository.findByHttpMethod(httpMethod.toUpperCase());
            return new ResponseEntity<>(logs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get query logs by response status
     */
    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<QueryLog>> getQueryLogsByStatus(@PathVariable int status) {
        try {
            List<QueryLog> logs = queryLogRepository.findByResponseStatus(status);
            return new ResponseEntity<>(logs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Search query logs by URI pattern
     */
    @GetMapping("/search")
    public ResponseEntity<List<QueryLog>> searchQueryLogsByUri(@RequestParam String uriPattern) {
        try {
            List<QueryLog> logs = queryLogRepository.findByRequestUriContaining(uriPattern);
            return new ResponseEntity<>(logs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete all query logs (admin function)
     */
    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteAllQueryLogs() {
        try {
            queryLogRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get query log statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<QueryLogStats> getQueryLogStats() {
        try {
            long totalLogs = queryLogRepository.count();
            long successfulLogs = queryLogRepository.findBySuccess(true).size();
            long failedLogs = queryLogRepository.findBySuccess(false).size();
            
            QueryLogStats stats = new QueryLogStats();
            stats.setTotal(totalLogs);
            stats.setSuccessful(successfulLogs);
            stats.setFailed(failedLogs);
            stats.setSuccessRate(totalLogs > 0 ? (double) successfulLogs / totalLogs * 100 : 0);
            
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Inner class for statistics response
     */
    public static class QueryLogStats {
        private long total;
        private long successful;
        private long failed;
        private double successRate;

        // Getters and setters
        public long getTotal() { return total; }
        public void setTotal(long total) { this.total = total; }

        public long getSuccessful() { return successful; }
        public void setSuccessful(long successful) { this.successful = successful; }

        public long getFailed() { return failed; }
        public void setFailed(long failed) { this.failed = failed; }

        public double getSuccessRate() { return successRate; }
        public void setSuccessRate(double successRate) { this.successRate = successRate; }
    }
}
