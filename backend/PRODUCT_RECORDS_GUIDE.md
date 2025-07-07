# Product Records Guide

This guide explains how to use the sample product JSON files with your e-commerce product management API.

## Available Sample Files

1. **product-sample-1.json** - Detailed wireless headphones with specifications
2. **product-sample-2.json** - Simple organic cotton t-shirt
3. **products-bulk-sample.json** - Array of 5 diverse products for bulk testing

## How to Add Products Using the Sample Data

### Method 1: Using PowerShell/curl

```powershell
# Add single product (headphones)
$headers = @{ "Content-Type" = "application/json" }
$body = Get-Content -Path "product-sample-1.json" -Raw
Invoke-RestMethod -Uri "http://localhost:8080/api/products" -Method POST -Headers $headers -Body $body

# Add single product (t-shirt)
$body = Get-Content -Path "product-sample-2.json" -Raw
Invoke-RestMethod -Uri "http://localhost:8080/api/products" -Method POST -Headers $headers -Body $body
```

### Method 2: Using curl

```bash
# Add single product
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d @product-sample-1.json

# Add another product
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d @product-sample-2.json
```

### Method 3: Bulk Adding (PowerShell script)

```powershell
# Load bulk products and add each one
$products = Get-Content -Path "products-bulk-sample.json" -Raw | ConvertFrom-Json
$headers = @{ "Content-Type" = "application/json" }

foreach ($product in $products) {
    $body = $product | ConvertTo-Json -Depth 10
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:8080/api/products" -Method POST -Headers $headers -Body $body
        Write-Host "Added product: $($product.name) - ID: $($response.id)"
    }
    catch {
        Write-Host "Failed to add product: $($product.name) - Error: $($_.Exception.Message)"
    }
}
```

## Testing the Added Products

### Get All Products
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/products" -Method GET
```

### Search Products
```powershell
# Search by name
Invoke-RestMethod -Uri "http://localhost:8080/api/products/search?name=headphones" -Method GET

# Search by category
Invoke-RestMethod -Uri "http://localhost:8080/api/products/search?category=Electronics" -Method GET

# Search by price range
Invoke-RestMethod -Uri "http://localhost:8080/api/products/search?minPrice=20&maxPrice=100" -Method GET
```

### Get Product by ID
```powershell
# Replace {id} with actual product ID
Invoke-RestMethod -Uri "http://localhost:8080/api/products/{id}" -Method GET
```

## Modifying Sample Data

You can easily modify the JSON files to:
- Change product names, descriptions, or prices
- Add different categories
- Modify stock quantities
- Add custom tags or specifications
- Set products as active/inactive

## Product Data Structure

Each product should include:
- **name** (required): Product name
- **description**: Detailed product description
- **price** (required): Product price as decimal
- **category**: Category object with name and description
- **stockQuantity**: Available inventory count
- **sku**: Stock Keeping Unit (unique identifier)
- **brand**: Product brand name
- **tags**: Array of searchable tags
- **active**: Boolean to enable/disable product
- **specifications**: Additional product details (optional)

## Notes

- Make sure your Spring Boot application is running on `http://localhost:8080`
- The API will automatically generate IDs for new products
- Categories will be created automatically if they don't exist
- All sample data follows the Product model structure in your application
