# E-commerce Product Management API Test Script
# This script tests all the REST endpoints of your application

param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$Username = "user",
    [string]$Password = "537046fd-9861-46ac-8186-b2270383727c"
)

Write-Host "=== E-commerce Product Management API Tests ===" -ForegroundColor Cyan
Write-Host "Base URL: $BaseUrl" -ForegroundColor Yellow

# Create authentication header
$cred = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("${Username}:${Password}"))
$headers = @{Authorization = "Basic $cred"}

# Test counters
$totalTests = 0
$passedTests = 0
$failedTests = 0

function Test-Endpoint {
    param(
        [string]$TestName,
        [string]$Method,
        [string]$Url,
        [hashtable]$Headers,
        [object]$Body = $null,
        [int]$ExpectedStatus = 200
    )
    
    $global:totalTests++
    Write-Host "`n--- Testing: $TestName ---" -ForegroundColor White
    
    try {
        $params = @{
            Uri = $Url
            Method = $Method
            Headers = $Headers
        }
        
        if ($Body) {
            $params.Body = ($Body | ConvertTo-Json)
            $params.ContentType = "application/json"
        }
        
        $response = Invoke-RestMethod @params -ErrorAction Stop
        Write-Host "✅ PASS: $TestName" -ForegroundColor Green
        Write-Host "   Response: $($response | ConvertTo-Json -Depth 2)" -ForegroundColor Gray
        $global:passedTests++
        return $response
    }
    catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        if ($statusCode -eq $ExpectedStatus) {
            Write-Host "✅ PASS: $TestName (Expected error $ExpectedStatus)" -ForegroundColor Green
            $global:passedTests++
        } else {
            Write-Host "❌ FAIL: $TestName" -ForegroundColor Red
            Write-Host "   Expected Status: $ExpectedStatus, Got: $statusCode" -ForegroundColor Red
            Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
            $global:failedTests++
        }
    }
}

function Test-PerformanceEndpoint {
    param(
        [string]$TestName,
        [string]$Url,
        [hashtable]$Headers,
        [int]$Iterations = 10
    )
    
    Write-Host "`n--- Performance Test: $TestName ---" -ForegroundColor Yellow
    
    $times = @()
    
    for ($i = 1; $i -le $Iterations; $i++) {
        $stopwatch = [System.Diagnostics.Stopwatch]::StartNew()
        try {
            Invoke-RestMethod -Uri $Url -Headers $Headers -Method GET -ErrorAction Stop | Out-Null
            $stopwatch.Stop()
            $times += $stopwatch.ElapsedMilliseconds
        }
        catch {
            Write-Host "   Request $i failed" -ForegroundColor Red
        }
    }
    
    if ($times.Count -gt 0) {
        $avgTime = ($times | Measure-Object -Average).Average
        $minTime = ($times | Measure-Object -Minimum).Minimum
        $maxTime = ($times | Measure-Object -Maximum).Maximum
        
        Write-Host "   Average response time: $($avgTime.ToString('F2'))ms" -ForegroundColor Cyan
        Write-Host "   Min: $($minTime)ms, Max: $($maxTime)ms" -ForegroundColor Cyan
    }
}

# Start tests
Write-Host "`n🚀 Starting API Tests..." -ForegroundColor Cyan

# Test 1: Health Check
Test-Endpoint -TestName "Health Check" -Method "GET" -Url "$BaseUrl/actuator/health" -Headers @{}

# Test 2: Get All Products
$products = Test-Endpoint -TestName "Get All Products" -Method "GET" -Url "$BaseUrl/api/products" -Headers $headers

# Test 3: Create a new product
$newProduct = @{
    name = "API Test Product"
    description = "Product created via API test script"
    price = 299.99
    quantity = 50
    sku = "API-TEST-001"
    category = "Electronics"
    brand = "TestBrand"
}

$createdProduct = Test-Endpoint -TestName "Create Product" -Method "POST" -Url "$BaseUrl/api/products" -Headers $headers -Body $newProduct -ExpectedStatus 201

# Test 4: Get product by ID (using created product)
if ($createdProduct -and $createdProduct.id) {
    Test-Endpoint -TestName "Get Product by ID" -Method "GET" -Url "$BaseUrl/api/products/$($createdProduct.id)" -Headers $headers
}

# Test 5: Update product
if ($createdProduct -and $createdProduct.id) {
    $updatedProduct = @{
        name = "Updated API Test Product"
        description = "Updated product description"
        price = 399.99
        quantity = 75
        sku = "API-TEST-001-UPDATED"
        category = "Electronics"
        brand = "TestBrand"
    }
    
    Test-Endpoint -TestName "Update Product" -Method "PUT" -Url "$BaseUrl/api/products/$($createdProduct.id)" -Headers $headers -Body $updatedProduct
}

# Test 6: Search products
Test-Endpoint -TestName "Search Products by Name" -Method "GET" -Url "$BaseUrl/api/products/search?name=iPhone" -Headers $headers

# Test 7: Get products by price range
Test-Endpoint -TestName "Get Products by Price Range" -Method "GET" -Url "$BaseUrl/api/products/price-range?minPrice=100&maxPrice=500" -Headers $headers

# Test 8: Get low stock products
Test-Endpoint -TestName "Get Low Stock Products" -Method "GET" -Url "$BaseUrl/api/products/low-stock?threshold=20" -Headers $headers

# Test 9: Get products by category
Test-Endpoint -TestName "Get Products by Category" -Method "GET" -Url "$BaseUrl/api/products/category/Electronics" -Headers $headers

# Test 10: Update inventory
if ($createdProduct -and $createdProduct.id) {
    Test-Endpoint -TestName "Update Inventory" -Method "PATCH" -Url "$BaseUrl/api/products/$($createdProduct.id)/inventory?quantity=100" -Headers $headers
}

# Test 11: Invalid requests (should fail)
Test-Endpoint -TestName "Get Non-existent Product" -Method "GET" -Url "$BaseUrl/api/products/non-existent-id" -Headers $headers -ExpectedStatus 404

Test-Endpoint -TestName "Create Invalid Product" -Method "POST" -Url "$BaseUrl/api/products" -Headers $headers -Body @{name=""; price=-10} -ExpectedStatus 400

# Test 12: Unauthorized access
Test-Endpoint -TestName "Unauthorized Access" -Method "GET" -Url "$BaseUrl/api/products" -Headers @{} -ExpectedStatus 401

# Performance Tests
Write-Host "`n⚡ Performance Tests..." -ForegroundColor Cyan

Test-PerformanceEndpoint -TestName "Get All Products Performance" -Url "$BaseUrl/api/products" -Headers $headers -Iterations 5
Test-PerformanceEndpoint -TestName "Search Performance" -Url "$BaseUrl/api/products/search?name=iPhone" -Headers $headers -Iterations 5

# Test 13: Rate Limiting (optional - will hit rate limits)
Write-Host "`n--- Rate Limiting Test ---" -ForegroundColor Yellow
Write-Host "Making 15 rapid requests to test rate limiting..." -ForegroundColor Gray

$rateLimitHit = $false
for ($i = 1; $i -le 15; $i++) {
    try {
        $response = Invoke-RestMethod -Uri "$BaseUrl/api/products" -Headers $headers -Method GET -ErrorAction Stop
        Write-Host "   Request $i: Success" -ForegroundColor Green
    }
    catch {
        $statusCode = $_.Exception.Response.StatusCode.value__
        if ($statusCode -eq 429) {
            Write-Host "   Request $i: Rate limited (429)" -ForegroundColor Yellow
            $rateLimitHit = $true
        } else {
            Write-Host "   Request $i: Error ($statusCode)" -ForegroundColor Red
        }
    }
    Start-Sleep -Milliseconds 100
}

if ($rateLimitHit) {
    Write-Host "✅ Rate limiting is working!" -ForegroundColor Green
} else {
    Write-Host "⚠️  Rate limiting may not be active or limits not reached" -ForegroundColor Yellow
}

# Clean up: Delete the test product
if ($createdProduct -and $createdProduct.id) {
    Test-Endpoint -TestName "Delete Test Product" -Method "DELETE" -Url "$BaseUrl/api/products/$($createdProduct.id)" -Headers $headers -ExpectedStatus 204
}

# Final Results
Write-Host "`n" + "="*50 -ForegroundColor Cyan
Write-Host "🏁 TEST RESULTS" -ForegroundColor Cyan
Write-Host "="*50 -ForegroundColor Cyan
Write-Host "Total Tests: $totalTests" -ForegroundColor White
Write-Host "Passed: $passedTests" -ForegroundColor Green
Write-Host "Failed: $failedTests" -ForegroundColor Red

if ($failedTests -eq 0) {
    Write-Host "`n🎉 ALL TESTS PASSED! Your API is working great!" -ForegroundColor Green
} else {
    Write-Host "`n⚠️  Some tests failed. Check the details above." -ForegroundColor Yellow
}

Write-Host "`n📊 API Endpoints Summary:" -ForegroundColor Cyan
Write-Host "   • Health Check: $BaseUrl/actuator/health" -ForegroundColor Gray
Write-Host "   • Products API: $BaseUrl/api/products" -ForegroundColor Gray
Write-Host "   • Search: $BaseUrl/api/products/search?name=..." -ForegroundColor Gray
Write-Host "   • Price Range: $BaseUrl/api/products/price-range?minPrice=...&maxPrice=..." -ForegroundColor Gray
Write-Host "   • Low Stock: $BaseUrl/api/products/low-stock?threshold=..." -ForegroundColor Gray
Write-Host "   • Category: $BaseUrl/api/products/category/{category}" -ForegroundColor Gray
Write-Host "   • Brand: $BaseUrl/api/products/brand/{brand}" -ForegroundColor Gray
Write-Host "   • Inventory Update: $BaseUrl/api/products/{id}/inventory?quantity=..." -ForegroundColor Gray
