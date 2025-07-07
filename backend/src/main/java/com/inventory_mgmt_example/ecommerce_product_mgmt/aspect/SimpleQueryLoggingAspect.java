package com.inventory_mgmt_example.ecommerce_product_mgmt.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Aspect
// @Component  // Disabled to avoid conflict with QueryLoggingAspect
public class SimpleQueryLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(SimpleQueryLoggingAspect.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
        
        // Log request start
        logger.info("=== API REQUEST START ===");
        logger.info("Timestamp: {}", LocalDateTime.now().format(formatter));
        logger.info("Client IP: {}", clientIp);
        logger.info("HTTP Method: {}", httpMethod);
        logger.info("Request URI: {}", requestUri);
        logger.info("Query String: {}", queryString != null ? queryString : "None");
        logger.info("Controller Method: {}.{}", className, methodName);
        logger.info("Method Arguments: {}", Arrays.toString(args));
        logger.info("User Agent: {}", userAgent);
        
        Object result = null;
        boolean success = true;
        String errorMessage = null;
        int responseStatus = 200;
        
        try {
            // Execute the actual method
            result = joinPoint.proceed();
            
            // Get response details
            if (result instanceof ResponseEntity) {
                ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
                responseStatus = responseEntity.getStatusCode().value();
            }
            
        } catch (Exception e) {
            success = false;
            errorMessage = e.getMessage();
            responseStatus = 500;
            logger.error("ERROR in method {}: {}", methodName, errorMessage);
            throw e; // Re-throw the exception
        } finally {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            // Log response
            logger.info("Response Status: {}", responseStatus);
            logger.info("Execution Time: {} ms", executionTime);
            logger.info("Success: {}", success);
            if (!success && errorMessage != null) {
                logger.info("Error Message: {}", errorMessage);
            }
            logger.info("=== API REQUEST END ===");
            logger.info(""); // Empty line for readability
            
            // Log summary for quick analysis
            if (success) {
                logger.info("SUMMARY: {} {} - {} - {}ms - SUCCESS", 
                        httpMethod, requestUri, responseStatus, executionTime);
            } else {
                logger.warn("SUMMARY: {} {} - {} - {}ms - FAILED: {}", 
                        httpMethod, requestUri, responseStatus, executionTime, errorMessage);
            }
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
     * Log method entry for debugging
     */
    @Before("productControllerMethods()")
    public void logMethodEntry(JoinPoint joinPoint) {
        logger.debug("→ Entering method: {} with arguments: {}", 
                joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
    }

    /**
     * Log successful method completion
     */
    @AfterReturning(pointcut = "productControllerMethods()", returning = "result")
    public void logMethodSuccess(JoinPoint joinPoint, Object result) {
        logger.debug("✓ Method {} completed successfully", joinPoint.getSignature().getName());
    }

    /**
     * Log method exceptions
     */
    @AfterThrowing(pointcut = "productControllerMethods()", throwing = "exception")
    public void logMethodException(JoinPoint joinPoint, Exception exception) {
        logger.error("✗ Method {} threw exception: {}", 
                joinPoint.getSignature().getName(), exception.getMessage());
    }
}
