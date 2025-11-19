Write-Host "=== BACKEND DEBUG TEST ===" -ForegroundColor Green
Write-Host "=========================" -ForegroundColor Green

# Test 1: Basic connectivity
Write-Host "`n1. Testing basic connectivity..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/test" -Method GET
    Write-Host "✓ Backend is running!" -ForegroundColor Green
} catch {
    Write-Host "✗ Backend is not running!" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    exit
}

# Test 2: Database connection
Write-Host "`n2. Testing database connection..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/db-test" -Method GET
    Write-Host "✓ Database connection successful!" -ForegroundColor Green
    Write-Host "Response: $($response.Content)" -ForegroundColor Cyan
} catch {
    Write-Host "✗ Database connection failed!" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response: $responseBody" -ForegroundColor Red
    }
}

# Test 3: Registration with minimal data
Write-Host "`n3. Testing registration with minimal data..." -ForegroundColor Yellow
$headers = @{
    "Content-Type" = "application/json"
}

$body = @{
    username = "testuser123"
    password = "password123"
    email = "testuser123@example.com"
} | ConvertTo-Json

Write-Host "Request body: $body" -ForegroundColor Cyan

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/register" -Method POST -Headers $headers -Body $body
    Write-Host "✓ Registration successful!" -ForegroundColor Green
    Write-Host "Response: $($response.Content)" -ForegroundColor Green
} catch {
    Write-Host "✗ Registration failed!" -ForegroundColor Red
    Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response Body: $responseBody" -ForegroundColor Red
    }
}

Write-Host "`n=== TEST COMPLETED ===" -ForegroundColor Green
Write-Host "Check the backend console for detailed logs!" -ForegroundColor Yellow 