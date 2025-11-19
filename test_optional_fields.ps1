Write-Host "=== TESTING OPTIONAL FIELDS ===" -ForegroundColor Green
Write-Host "===============================" -ForegroundColor Green

$headers = @{
    "Content-Type" = "application/json"
}

# Test with all optional fields filled
$body = @{
    username = "testuser999"
    password = "password123"
    email = "testuser999@example.com"
    phoneNumber = "9876543210"
    firstname = "John"
    lastname = "Doe"
    role = "USER"
} | ConvertTo-Json

Write-Host "Testing registration with all optional fields..." -ForegroundColor Yellow
Write-Host "Request body: $body" -ForegroundColor Cyan

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/register" -Method POST -Headers $headers -Body $body
    Write-Host "✓ Registration successful!" -ForegroundColor Green
    Write-Host "Response: $($response.Content)" -ForegroundColor Green
    Write-Host "Check the backend console for DEBUG messages!" -ForegroundColor Yellow
    Write-Host "Check the database to verify fields are saved!" -ForegroundColor Yellow
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

Write-Host "`n=== TESTING WITH PARTIAL FIELDS ===" -ForegroundColor Green

# Test with some optional fields empty
$body2 = @{
    username = "testuser888"
    password = "password123"
    email = "testuser888@example.com"
    phoneNumber = "1234567890"
    firstname = "Jane"
    # lastname is intentionally omitted
    role = "USER"
} | ConvertTo-Json

Write-Host "Testing registration with partial optional fields..." -ForegroundColor Yellow
Write-Host "Request body: $body2" -ForegroundColor Cyan

try {
    $response2 = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/register" -Method POST -Headers $headers -Body $body2
    Write-Host "✓ Partial registration successful!" -ForegroundColor Green
    Write-Host "Response: $($response2.Content)" -ForegroundColor Green
} catch {
    Write-Host "✗ Partial registration failed!" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
} 