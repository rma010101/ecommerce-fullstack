package com.inventory_mgmt_example.ecommerce_product_mgmt.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitingConfig {

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        return RateLimiterRegistry.ofDefaults();
    }

    @Bean
    public RateLimiter productApiRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .limitForPeriod(100) // 100 requests per minute
                .timeoutDuration(Duration.ofSeconds(5))
                .build();
        
        return RateLimiter.of("product-api", config);
    }

    @Bean
    public RateLimiter bulkOperationRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .limitForPeriod(10) // 10 bulk operations per minute
                .timeoutDuration(Duration.ofSeconds(10))
                .build();
        
        return RateLimiter.of("bulk-operations", config);
    }

    @Bean
    public RateLimiter searchRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .limitForPeriod(200) // 200 search requests per minute
                .timeoutDuration(Duration.ofSeconds(3))
                .build();
        
        return RateLimiter.of("search-api", config);
    }
}
