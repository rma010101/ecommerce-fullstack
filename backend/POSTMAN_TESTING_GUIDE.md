# 🚀 Postman Testing Guide for E-commerce Product Management API

This guide provides step-by-step instructions for testing your Spring Boot API using Postman.

## 📥 Quick Setup

### 1. Import the Collection
1. **Open Postman**
2. **Click "Import"** (top left)
3. **Select "Upload Files"**
4. **Choose** `Postman_Collection.json` from your project folder
5. **Click "Import"**

### 2. Set Up Environment Variables
1. **Click the "Environment" tab** (left sidebar)
2. **Create New Environment** called "E-commerce Local"
3. **Add these variables**:
   ```
   Variable Name: base_url
   Initial Value: http://localhost:8080
   Current Value: http://localhost:8080
   
   Variable Name: auth_password
   Initial Value: your-generated-password-here
   Current Value: your-generated-password-here
   
   Variable Name: product_id
   Initial Value: (leave empty)
   Current Value: (leave empty)
   ```
4. **Save** the environment
5. **Select** this environment from the dropdown (top right)

### 3. Get Your Authentication Password
1. **Start your Spring Boot application**
2. **Check the console logs** for a line like:
   ```
   Using generated security password: 537046fd-9861-46ac-8186-b2270383727c
   ```
3. **Copy this password** and update the `auth_password` variable in Postman

## 🧪 Test Collection Overview

### **6 Test Categories with 20+ Requests:**

### 1. **Health & Monitoring** (3 tests)
- ✅ Health Check
- ✅ Application Info  
- ✅ Metrics

### 2. **Product Management** (5 tests)
- ✅ Get All Products
- ✅ Create Product
- ✅ Get Product by ID
- ✅ Update Product
- ✅ Delete Product

### 3. **Search & Filtering** (5 tests)
- ✅ Search Products by Name
- ✅ Get Products by Price Range
- ✅ Get Low Stock Products
- ✅ Get Products by Category
- ✅ Get Products by Brand

### 4. **Inventory Management** (2 tests)
- ✅ Update Product Inventory
- ✅ Bulk Create Products

### 5. **Error Testing** (3 tests)
- ✅ Get Non-existent Product (404)
- ✅ Create Invalid Product (400)
- ✅ Unauthorized Request (401)

### 6. **Performance Testing** (1 test)
- ✅ Load Test - Response Time Check

## 🎯 Step-by-Step Testing Instructions

### **Phase 1: Basic Connectivity**
1. **Run "Health Check"** first
   - Should return status 200
   - Confirms application is running

2. **Run "Get All Products"**
   - Should return status 200 with array of products
   - Tests basic authentication

### **Phase 2: CRUD Operations**
3. **Run "Create Product"**
   - Creates a test product
   - Automatically saves product ID for later tests
   - Should return status 201

4. **Run "Get Product by ID"**
   - Uses the ID from step 3
   - Should return status 200 with product details

5. **Run "Update Product"**
   - Updates the created product
   - Should return status 200 with updated data

6. **Run "Delete Product"**
   - Deletes the test product
   - Should return status 204 (No Content)

### **Phase 3: Search & Filtering**
7. **Run all search tests**
   - Search by name, price range, category, etc.
   - All should return status 200

### **Phase 4: Advanced Features**
8. **Run "Bulk Create Products"**
   - Creates multiple products at once
   - Should return status 201 with array

9. **Run "Update Product Inventory"**
   - Updates product quantity
   - Should return status 200

### **Phase 5: Error Handling**
10. **Run all error tests**
    - Tests 404, 400, and 401 responses
    - Validates proper error handling

## 🔍 Automated Test Assertions

Each request includes **automated tests** that validate:

### ✅ **Status Code Validation**
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});
```

### ✅ **Response Time Validation**
```javascript
pm.test("Response time is less than 2000ms", function () {
    pm.expect(pm.response.responseTime).to.be.below(2000);
});
```

### ✅ **Data Validation**
```javascript
pm.test("Product created successfully", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('id');
    pm.expect(jsonData.name).to.eql('Postman Test Product');
});
```

### ✅ **Business Logic Validation**
```javascript
pm.test("Products are within price range", function () {
    var jsonData = pm.response.json();
    jsonData.forEach(function(product) {
        pm.expect(product.price).to.be.at.least(100);
        pm.expect(product.price).to.be.at.most(500);
    });
});
```

## 🏃‍♂️ Running Tests

### **Individual Test**
1. **Select a request** from the collection
2. **Click "Send"**
3. **Check the "Test Results" tab** for pass/fail status

### **Run Entire Collection**
1. **Click the "..." menu** next to collection name
2. **Select "Run collection"**
3. **Click "Run E-commerce Product Management API"**
4. **View the test results** in the runner

### **Collection Runner Results**
- ✅ **Pass Count**: Number of successful tests
- ❌ **Fail Count**: Number of failed tests  
- ⏱️ **Average Response Time**: Performance metric
- 📊 **Test Coverage**: Percentage of API covered

## 📋 Sample Test Data

The collection includes realistic test data:

### **Product Creation**
```json
{
  "name": "Postman Test Product",
  "description": "This product was created via Postman for testing purposes",
  "price": 299.99,
  "quantity": 50,
  "sku": "POSTMAN-001",
  "category": "Electronics",
  "brand": "TestBrand"
}
```

### **Bulk Product Creation**
```json
[
  {
    "name": "Bulk Product 1",
    "description": "First bulk created product",
    "price": 199.99,
    "quantity": 30,
    "sku": "BULK-001",
    "category": "Electronics",
    "brand": "BulkBrand"
  },
  // ... more products
]
```

## 🐛 Troubleshooting

### **Common Issues:**

1. **401 Unauthorized**
   - ❌ **Problem**: Wrong password
   - ✅ **Solution**: Update `auth_password` variable with current generated password

2. **Connection Refused**
   - ❌ **Problem**: Application not running
   - ✅ **Solution**: Start Spring Boot application with `mvn spring-boot:run`

3. **404 Not Found for product ID**
   - ❌ **Problem**: Product doesn't exist
   - ✅ **Solution**: Run "Create Product" first to generate a valid ID

4. **Rate Limiting (429)**
   - ❌ **Problem**: Too many requests
   - ✅ **Solution**: Wait a minute or adjust rate limits in config

### **Validation Checklist:**

Before testing, ensure:
- [ ] Spring Boot application is running (localhost:8080)
- [ ] MongoDB is running (localhost:27017)
- [ ] Postman environment is set up correctly
- [ ] Authentication password is current
- [ ] Base URL points to correct server

## 🎉 Expected Results

### **Successful Test Run Should Show:**
- ✅ **20+ tests passing**
- ⏱️ **Response times < 2 seconds**
- 📊 **All HTTP status codes as expected**
- 🔒 **Authentication working properly**
- 🛡️ **Error handling functioning correctly**

### **Performance Benchmarks:**
- **Health Check**: < 100ms
- **Get All Products**: < 1000ms
- **Create Product**: < 500ms
- **Search Operations**: < 1000ms
- **Update Operations**: < 500ms

## 🚀 Advanced Testing

### **Load Testing**
Run the same request multiple times:
1. **Use Collection Runner**
2. **Set Iterations** to 10-50
3. **Monitor response times**
4. **Check for rate limiting**

### **Data-Driven Testing**
1. **Export test data** to CSV
2. **Use Postman's data file feature**
3. **Run tests with multiple data sets**

Your API is now fully testable with professional-grade validation! 🎯
