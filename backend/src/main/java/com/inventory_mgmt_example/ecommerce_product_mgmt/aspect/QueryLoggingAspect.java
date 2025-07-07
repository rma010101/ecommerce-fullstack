package com.inventory_mgmt_example.ecommerce_product_mgmt.aspect;

import com.inventory_mgmt_example.ecommerce_product_mgmt.model.QueryLog;
import com.inventory_mgmt_example.ecommerce_product_mgmt.repository.QueryLogRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
public class QueryLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(QueryLoggingAspect.class);

    @Autowired
    private QueryLogRepository queryLogRepository;

    /**
     * Pointcut for all controller methods in ProductController
     */
    @Pointcut("execution(* com.inventory_mgmt_example.ecommerce_product_mgmt.controller.ProductController.*(..))")
    public void productControllerMethods() {}

    /**
     * Around advice to log all API calls
     */
    @Around("productControllerMethods()")
    public Object logQueryActivity(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();
        
        // Get HTTP request details
        HttpServletRequest request = getCurrentHttpRequest();
        String clientIp = getClientIpAddress(request);
        String httpMethod = request != null ? request.getMethod() : "UNKNOWN";
        String requestUri = request != null ? request.getRequestURI() : "UNKNOWN";
        String queryString = request != null ? request.getQueryString() : "";
        String userAgent = request != null ? request.getHeader("User-Agent") : "UNKNOWN";
        
        QueryLog queryLog = new QueryLog();
        queryLog.setTimestamp(LocalDateTime.now());
        queryLog.setClientIp(clientIp);
        queryLog.setHttpMethod(httpMethod);
        queryLog.setRequestUri(requestUri);
        queryLog.setQueryString(queryString);
        queryLog.setControllerMethod(className + "." + methodName);
        queryLog.setMethodArguments(Arrays.toString(args));
        queryLog.setUserAgent(userAgent);
        
        Object result = null;
        boolean success = true;
        String errorMessage = null;
        
        try {
            // Execute the actual method
            result = joinPoint.proceed();
            
            // Log response details
            if (result instanceof ResponseEntity) {
                ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
                queryLog.setResponseStatus(responseEntity.getStatusCode().value());
                queryLog.setResponseSize(calculateResponseSize(responseEntity.getBody()));
            }
            
        } catch (Exception e) {
            success = false;
            errorMessage = e.getMessage();
            queryLog.setResponseStatus(500);
            queryLog.setErrorMessage(errorMessage);
            logger.error("Error in method {}: {}", methodName, errorMessage, e);
            throw e; // Re-throw the exception
        } finally {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            queryLog.setExecutionTimeMs(executionTime);
            queryLog.setSuccess(success);
            
            // Save the log asynchronously to avoid impacting performance
            saveQueryLogAsync(queryLog);
            
            // Also log to console for debugging
            logger.info("API Call: {} {} - Method: {} - IP: {} - Duration: {}ms - Status: {}", 
                    httpMethod, requestUri, methodName, clientIp, executionTime, 
                    queryLog.getResponseStatus());
        }
        
        return result;
    }

    /**
     * Get the current HTTP request
     */
    private HttpServletRequest getCurrentHttpRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attributes.getRequest();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get client IP address, considering proxy headers
     */
    private String getClientIpAddress(HttpServletRequest request) {
        if (request == null) return "UNKNOWN";
        
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    /**
     * Calculate approximate response size
     */
    private long calculateResponseSize(Object responseBody) {
        if (responseBody == null) return 0;
        return responseBody.toString().length();
    }

    /**
     * Save query log asynchronously to avoid blocking the main thread
     */
    private void saveQueryLogAsync(QueryLog queryLog) {
        try {
            queryLogRepository.save(queryLog);
        } catch (Exception e) {
            logger.error("Failed to save query log: {}", e.getMessage());
        }
    }

    /**
     * Log method entry (optional - for detailed debugging)
     */
    @Before("productControllerMethods()")
    public void logMethodEntry(JoinPoint joinPoint) {
        logger.debug("Entering method: {} with arguments: {}", 
                joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
    }

    /**
     * Log successful method completion
     */
    @AfterReturning(pointcut = "productControllerMethods()", returning = "result")
    public void logMethodSuccess(JoinPoint joinPoint, Object result) {
        logger.debug("Method {} completed successfully", joinPoint.getSignature().getName());
    }

    /**
     * Log method exceptions
     */
    @AfterThrowing(pointcut = "productControllerMethods()", throwing = "exception")
    public void logMethodException(JoinPoint joinPoint, Exception exception) {
        logger.error("Method {} threw exception: {}", 
                joinPoint.getSignature().getName(), exception.getMessage());
    }
}
