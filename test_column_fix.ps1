Write-Host "=== TESTING COLUMN MAPPING FIX ===" -ForegroundColor Green
Write-Host "==================================" -ForegroundColor Green

$headers = @{
    "Content-Type" = "application/json"
}

# Test with all optional fields to ensure column mapping works
$body = @{
    username = "testuser456"
    password = "password123"
    email = "testuser456@example.com"
    phoneNumber = "1234567890"
    firstname = "Test"
    lastname = "User"
    role = "USER"
} | ConvertTo-Json

Write-Host "Testing registration with all fields..." -ForegroundColor Yellow
Write-Host "Request body: $body" -ForegroundColor Cyan

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/register" -Method POST -Headers $headers -Body $body
    Write-Host "✓ Registration successful!" -ForegroundColor Green
    Write-Host "Response: $($response.Content)" -ForegroundColor Green
    Write-Host "Column mapping fix is working!" -ForegroundColor Green
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