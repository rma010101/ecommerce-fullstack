# E-commerce Product Management System

A robust, enterprise-grade Spring Boot application for managing e-commerce products with comprehensive features including caching, rate limiting, monitoring, validation, and AOP-based logging.

## 🚀 Features

### Core Functionality
- **Full CRUD Operations** - Create, Read, Update, Delete products
- **Advanced Search** - Search products by name, description, category, or brand
- **Inventory Management** - Track and update product quantities
- **Data Validation** - Comprehensive input validation with custom error messages

### Enterprise Features
- **Caching** - High-performance caching with Caffeine
- **Rate Limiting** - API rate limiting with Resilience4j
- **AOP Logging** - Aspect-oriented query logging to MongoDB
- **Error Handling** - Global exception handling with structured responses
- **API Documentation** - Interactive Swagger/OpenAPI documentation
- **Monitoring** - Spring Boot Actuator with Prometheus metrics
- **Security** - Basic Spring Security configuration
- **Database Auditing** - Automatic timestamp tracking

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.5.3
- **Database**: MongoDB
- **Caching**: Caffeine
- **Rate Limiting**: Resilience4j
- **Documentation**: OpenAPI 3 / Swagger
- **Monitoring**: Spring Boot Actuator + Prometheus
- **Security**: Spring Security
- **Validation**: Bean Validation (Jakarta)
- **AOP**: Spring AOP + AspectJ

## 📋 Prerequisites

- Java 24+
- Maven 3.6+
- MongoDB 4.4+
- IDE (IntelliJ IDEA, Eclipse, VS Code)

## 🏃‍♂️ Quick Start

### 1. Clone and Setup
```bash
# Navigate to your project directory
cd ecommerce_product_mgmt

# Install dependencies
mvn clean install
```

### 2. Start MongoDB
```bash
# Using Docker
docker run -d -p 27017:27017 --name mongodb mongo:latest

# Or install locally and start the service
mongod
```

### 3. Run the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Test the Features
```powershell
# Run the PowerShell test script (Windows)
.\test-features.ps1

# Or run the Bash script (Linux/Mac)
chmod +x test-features.sh
./test-features.sh
```

## 📚 API Documentation

Once the application is running, visit:
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 🔗 Key Endpoints

### Product Management
- `GET /api/products` - Get all products (cached)
- `POST /api/products` - Create a new product
- `GET /api/products/{id}` - Get product by ID (cached)
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product (clears cache)
- `PATCH /api/products/{id}/inventory` - Update inventory

### Search & Filtering
- `GET /api/products/search?query={term}` - Search products
- `GET /api/products/category/{category}` - Filter by category
- `GET /api/products/brand/{brand}` - Filter by brand

### Monitoring & Logging
- `GET /api/query-logs` - View AOP query logs
- `GET /actuator/health` - Health check
- `GET /actuator/metrics` - Application metrics
- `GET /actuator/prometheus` - Prometheus metrics
- `GET /actuator/caches` - Cache information

## 🏗️ Project Structure

```
src/
├── main/java/com/inventory_mgmt_example/ecommerce_product_mgmt/
│   ├── EcommerceProductMgmtApplication.java     # Main application class
│   ├── config/
│   │   ├── CacheConfig.java                     # Cache configuration
│   │   ├── OpenApiConfig.java                   # API documentation config
│   │   ├── RateLimitingConfig.java              # Rate limiting config
│   │   └── SecurityConfig.java                  # Security configuration
│   ├── controller/
│   │   ├── ProductController.java               # REST API endpoints
│   │   └── QueryLogController.java              # Query log endpoints
│   ├── dto/
│   │   └── ProductCreateDTO.java                # Data transfer objects
│   ├── model/
│   │   ├── Product.java                         # Product entity
│   │   └── QueryLog.java                        # Query log entity
│   ├── repository/
│   │   ├── ProductRepository.java               # Product data access
│   │   └── QueryLogRepository.java              # Query log data access
│   ├── service/
│   │   └── ProductService.java                  # Business logic layer
│   ├── aspect/
│   │   ├── QueryLoggingAspect.java              # AOP logging (active)
│   │   └── SimpleQueryLoggingAspect.java        # Simple logging (disabled)
│   └── exception/
│       ├── GlobalExceptionHandler.java          # Global error handling
│       ├── ProductNotFoundException.java        # Custom exceptions
│       └── DuplicateProductException.java
├── test/java/
│   └── integration/
│       └── ProductManagementIntegrationTest.java # Comprehensive tests
└── resources/
    ├── application.properties                    # Main configuration
    └── application-test.properties               # Test configuration
```

## ⚙️ Configuration

### application.properties
```properties
# Database
spring.data.mongodb.uri=mongodb://localhost:27017/ecommerce_product_mgmt

# Rate Limiting
resilience4j.ratelimiter.instances.product-api.limit-for-period=10
resilience4j.ratelimiter.instances.product-api.limit-refresh-period=1s

# Monitoring
management.endpoints.web.exposure.include=health,info,metrics,prometheus,cache
management.prometheus.metrics.export.enabled=true

# Logging
logging.level.com.inventory_mgmt_example.ecommerce_product_mgmt.aspect.QueryLoggingAspect=INFO
```

## 🧪 Testing

### Run Unit Tests
```bash
mvn test
```

### Run Integration Tests
```bash
mvn test -Dtest=ProductManagementIntegrationTest
```

### Manual Testing Examples

#### Create a Product
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Gaming Laptop",
    "description": "High-performance gaming laptop with RTX graphics",
    "price": 1499.99,
    "quantity": 10,
    "sku": "GAMING-001",
    "category": "Electronics",
    "brand": "TechCorp"
  }'
```

#### Search Products
```bash
curl "http://localhost:8080/api/products/search?query=gaming"
```

#### Update Inventory
```bash
curl -X PATCH "http://localhost:8080/api/products/{id}/inventory?quantity=25"
```

## 📊 Monitoring & Observability

### Health Checks
- Application health: `/actuator/health`
- Database connectivity: Included in health check
- Cache status: `/actuator/caches`

### Metrics
- JVM metrics: Memory, CPU, threads
- HTTP request metrics: Request count, duration, status codes
- Database metrics: Connection pool, query performance
- Cache metrics: Hit rates, evictions, size
- Custom business metrics: Product operations

### Query Logging
- All API calls are logged with AOP
- Logs include: timestamp, endpoint, parameters, response status, execution time
- Stored in MongoDB for analysis
- Accessible via `/api/query-logs` endpoint

## 🔒 Security Features

- Basic Spring Security configuration
- CSRF protection disabled for API endpoints
- Rate limiting to prevent abuse
- Input validation to prevent injection attacks
- Structured error responses (no sensitive data leakage)

## 🚀 Performance Features

### Caching Strategy
- **Product List**: Cached for 10 minutes
- **Individual Products**: Cached for 30 minutes
- **Search Results**: Cached for 5 minutes
- **Cache Eviction**: Automatic on create/update/delete operations

### Rate Limiting
- **Default Limit**: 10 requests per second per API
- **Configurable**: Per endpoint and per user (when authentication is added)
- **Graceful Degradation**: Returns 429 status with retry information

## 🔧 Production Considerations

### Scaling
- Stateless design for horizontal scaling
- Database connection pooling
- Distributed caching support (Redis can be added)
- Load balancer ready

### Monitoring
- Prometheus metrics for alerting
- Health checks for load balancer
- Structured logging for centralized log management
- Performance metrics for capacity planning

### Security
- JWT authentication can be easily added
- HTTPS configuration ready
- Input validation and sanitization
- Rate limiting for DDoS protection

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support, email support@example.com or create an issue in the repository.

---

**Built with ❤️ using Spring Boot and modern enterprise patterns**
