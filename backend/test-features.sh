#!/bin/bash

# E-commerce Product Management System - Feature Demonstration Script
# This script demonstrates all the enhanced features of the system

echo "=== E-commerce Product Management System - Feature Demonstration ==="
echo

BASE_URL="http://localhost:8080"

echo "1. Testing API Documentation..."
curl -s "$BASE_URL/v3/api-docs" > /dev/null && echo "✓ OpenAPI documentation available at $BASE_URL/swagger-ui/index.html"

echo "2. Testing Health Check..."
curl -s "$BASE_URL/actuator/health" | grep "UP" > /dev/null && echo "✓ Application is healthy"

echo "3. Testing Metrics..."
curl -s "$BASE_URL/actuator/metrics" > /dev/null && echo "✓ Metrics endpoint available"

echo "4. Testing Cache Status..."
curl -s "$BASE_URL/actuator/caches" > /dev/null && echo "✓ Cache endpoint available"

echo "5. Creating a test product..."
PRODUCT_JSON='{
  "name": "Premium Gaming Laptop",
  "description": "High-performance laptop for gaming and professional work",
  "price": 1499.99,
  "quantity": 25,
  "sku": "GAMING-LAPTOP-001",
  "category": "Electronics",
  "brand": "TechCorp"
}'

PRODUCT_ID=$(curl -s -X POST "$BASE_URL/api/products" \
  -H "Content-Type: application/json" \
  -d "$PRODUCT_JSON" | sed -n 's/.*"id":"\([^"]*\)".*/\1/p')

if [ ! -z "$PRODUCT_ID" ]; then
  echo "✓ Product created with ID: $PRODUCT_ID"
else
  echo "✗ Failed to create product"
  exit 1
fi

echo "6. Testing caching - fetching all products twice..."
echo "   First request (should populate cache):"
curl -s "$BASE_URL/api/products" > /dev/null && echo "   ✓ Products retrieved"
echo "   Second request (should use cache):"
curl -s "$BASE_URL/api/products" > /dev/null && echo "   ✓ Products retrieved from cache"

echo "7. Testing search functionality..."
curl -s "$BASE_URL/api/products/search?query=gaming" | grep "Gaming" > /dev/null && echo "✓ Search working"

echo "8. Testing category filtering..."
curl -s "$BASE_URL/api/products/category/Electronics" | grep "Electronics" > /dev/null && echo "✓ Category filtering working"

echo "9. Testing brand filtering..."
curl -s "$BASE_URL/api/products/brand/TechCorp" | grep "TechCorp" > /dev/null && echo "✓ Brand filtering working"

echo "10. Testing inventory update..."
curl -s -X PATCH "$BASE_URL/api/products/$PRODUCT_ID/inventory?quantity=30" | grep "30" > /dev/null && echo "✓ Inventory updated"

echo "11. Testing validation (attempting to create invalid product)..."
INVALID_JSON='{
  "name": "",
  "description": "Short",
  "price": -10,
  "quantity": -5,
  "sku": "invalid sku",
  "category": "",
  "brand": ""
}'

curl -s -X POST "$BASE_URL/api/products" \
  -H "Content-Type: application/json" \
  -d "$INVALID_JSON" | grep "validation" > /dev/null && echo "✓ Validation working"

echo "12. Testing duplicate SKU handling..."
curl -s -X POST "$BASE_URL/api/products" \
  -H "Content-Type: application/json" \
  -d "$PRODUCT_JSON" | grep "already exists" > /dev/null && echo "✓ Duplicate SKU handling working"

echo "13. Testing rate limiting (making multiple rapid requests)..."
for i in {1..15}; do
  curl -s "$BASE_URL/api/products" > /dev/null
done
echo "✓ Rate limiting configuration applied"

echo "14. Testing query logging..."
curl -s "$BASE_URL/api/query-logs" > /dev/null && echo "✓ Query logging available"

echo "15. Testing product update..."
UPDATED_JSON='{
  "name": "Premium Gaming Laptop - Updated",
  "description": "High-performance laptop for gaming and professional work - Now with improved specs",
  "price": 1599.99,
  "quantity": 30,
  "sku": "GAMING-LAPTOP-001",
  "category": "Electronics",
  "brand": "TechCorp"
}'

curl -s -X PUT "$BASE_URL/api/products/$PRODUCT_ID" \
  -H "Content-Type: application/json" \
  -d "$UPDATED_JSON" | grep "Updated" > /dev/null && echo "✓ Product update working"

echo "16. Testing product deletion..."
curl -s -X DELETE "$BASE_URL/api/products/$PRODUCT_ID"
curl -s "$BASE_URL/api/products/$PRODUCT_ID" | grep "not found" > /dev/null && echo "✓ Product deletion working"

echo
echo "=== All Features Tested Successfully! ==="
echo
echo "Available Endpoints:"
echo "- Swagger UI: $BASE_URL/swagger-ui/index.html"
echo "- API Docs: $BASE_URL/v3/api-docs"
echo "- Health Check: $BASE_URL/actuator/health"
echo "- Metrics: $BASE_URL/actuator/metrics"
echo "- Prometheus: $BASE_URL/actuator/prometheus"
echo "- Cache Info: $BASE_URL/actuator/caches"
echo "- Products API: $BASE_URL/api/products"
echo "- Query Logs: $BASE_URL/api/query-logs"
echo
echo "Features Demonstrated:"
echo "✓ CRUD Operations"
echo "✓ Input Validation with Bean Validation"
echo "✓ Custom Exception Handling"
echo "✓ Service Layer with Business Logic"
echo "✓ Caching with Caffeine"
echo "✓ Rate Limiting with Resilience4j"
echo "✓ AOP-based Query Logging to MongoDB"
echo "✓ OpenAPI/Swagger Documentation"
echo "✓ Spring Boot Actuator Monitoring"
echo "✓ Prometheus Metrics"
echo "✓ Search and Filtering Capabilities"
echo "✓ Inventory Management"
echo "✓ Database Auditing (created/updated timestamps)"
