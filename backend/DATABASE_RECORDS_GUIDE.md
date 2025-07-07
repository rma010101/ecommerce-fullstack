# 📊 Sample Database Records Guide

This guide provides sample JSON records to populate your e-commerce MongoDB database with realistic product data.

## 📁 Available Sample Files

### 1. **Single Product Record** (`sample-product-record.json`)
- **Samsung Galaxy S24 Ultra** - Premium smartphone
- Comprehensive product data with all fields
- Perfect for testing individual product operations

### 2. **Bulk Products Collection** (`sample-products-bulk.json`)
- **5 diverse products** across different categories:
  - 💻 MacBook Pro 16-inch M3 Pro (Computers)
  - 🎧 Sony WH-1000XM5 Headphones (Audio)
  - 🎮 Nintendo Switch OLED (Gaming)
  - 🏠 Dyson V15 Vacuum (Home Appliances)
  - 🚗 Tesla Model Y Wheels (Automotive)

## 🚀 How to Add Records to Database

### **Method 1: Using Postman**

#### **Single Product:**
1. **Import your Postman collection**
2. **Open "Create Product" request**
3. **Copy JSON from `sample-product-record.json`**
4. **Paste into request body**
5. **Click Send**

#### **Bulk Products:**
1. **Open "Bulk Create Products" request**
2. **Copy JSON array from `sample-products-bulk.json`**
3. **Paste into request body**
4. **Click Send**

### **Method 2: Using MongoDB Compass**

1. **Open MongoDB Compass**
2. **Connect to `mongodb://localhost:27017`**
3. **Navigate to `ecommerce_product_mgmt` database**
4. **Select `products` collection**
5. **Click "Insert Document"**
6. **Paste JSON data**
7. **Click "Insert"**

### **Method 3: Using MongoDB Shell**

```bash
# Start MongoDB shell
mongosh

# Switch to your database
use ecommerce_product_mgmt

# Insert single product
db.products.insertOne({
  // paste content from sample-product-record.json here
})

# Insert multiple products
db.products.insertMany([
  // paste content from sample-products-bulk.json here
])
```

### **Method 4: Using REST API with curl**

#### **Single Product:**
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic $(echo -n 'user:your-password' | base64)" \
  -d @sample-product-record.json
```

#### **Bulk Products:**
```bash
curl -X POST http://localhost:8080/api/products/bulk \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic $(echo -n 'user:your-password' | base64)" \
  -d @sample-products-bulk.json
```

## 🎯 What These Records Test

### **Core Features:**
- ✅ **Product Creation** - All required fields
- ✅ **Data Validation** - Proper formats and constraints
- ✅ **Search Functionality** - Various categories and brands
- ✅ **Price Range Filtering** - Different price points ($349 - $4599)
- ✅ **Inventory Management** - Various stock levels
- ✅ **Category Management** - 5 different categories

### **Advanced Features:**
- ✅ **Complex Specifications** - Nested objects
- ✅ **Rich Metadata** - Tags, SEO, warranty info
- ✅ **Real-world Data** - Actual product specifications
- ✅ **Varied Stock Levels** - Some low stock (8 units) to high stock (45 units)

## 📋 Product Categories Covered

| Category | Product | Price Range | Stock Level |
|----------|---------|-------------|-------------|
| **Electronics** | Samsung Galaxy S24 Ultra | $1,299.99 | 25 units |
| **Computers** | MacBook Pro 16" | $2,499.99 | 15 units |
| **Audio** | Sony WH-1000XM5 | $399.99 | 45 units |
| **Gaming** | Nintendo Switch OLED | $349.99 | 30 units |
| **Home Appliances** | Dyson V15 Vacuum | $749.99 | 20 units |
| **Automotive** | Tesla Model Y Wheels | $4,599.99 | 8 units (low stock) |

## 🧪 Testing Scenarios

### **After adding these records, you can test:**

1. **Search by Category:**
   - Electronics: Samsung Galaxy
   - Computers: MacBook Pro
   - Audio: Sony headphones
   - Gaming: Nintendo Switch
   - Home Appliances: Dyson vacuum
   - Automotive: Tesla wheels

2. **Search by Brand:**
   - Samsung, Apple, Sony, Nintendo, Dyson, Tesla

3. **Price Range Filters:**
   - Budget: $300-$500 (Nintendo, Sony)
   - Mid-range: $700-$1,500 (Dyson, Samsung)
   - Premium: $2,000+ (MacBook, Tesla)

4. **Low Stock Alerts:**
   - Tesla wheels (8 units) should trigger low stock

5. **Inventory Management:**
   - Update quantities for each product
   - Test bulk operations

## 🔧 Customization

### **Modify the Records:**
You can customize these records by:
- **Changing prices** to test different ranges
- **Adjusting quantities** to test low stock scenarios
- **Adding new categories** for your business
- **Updating specifications** to match real products
- **Adding more tags** for better search testing

### **Add Your Own Products:**
Create similar JSON structures for your specific products:
```json
{
  "name": "Your Product Name",
  "description": "Detailed description...",
  "price": 99.99,
  "quantity": 100,
  "sku": "YOUR-SKU-001",
  "category": "Your Category",
  "brand": "Your Brand",
  "specifications": {
    // Add relevant specs
  },
  "tags": ["tag1", "tag2"],
  // Add other fields as needed
}
```

## ✅ Verification

### **After adding records, verify they're working:**

1. **Check total count:**
   ```bash
   # In MongoDB shell
   db.products.countDocuments()
   ```

2. **Test search functionality:**
   - Search for "Samsung" should return Galaxy S24
   - Search for "Apple" should return MacBook Pro
   - Price range $300-$500 should return 2 products

3. **Test category filtering:**
   - Electronics category should have Samsung Galaxy
   - Gaming category should have Nintendo Switch

4. **Test low stock alerts:**
   - Set threshold to 10, Tesla wheels should appear

Your database is now populated with realistic, comprehensive product data for thorough testing! 🎉
