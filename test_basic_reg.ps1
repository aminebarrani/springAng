Write-Host "=== TESTING BASIC REGISTRATION ===" -ForegroundColor Green
Write-Host "==================================" -ForegroundColor Green

$headers = @{
    "Content-Type" = "application/json"
}

# Test with only required fields
$body = @{
    username = "testuser789"
    password = "password123"
    email = "testuser789@example.com"
} | ConvertTo-Json

Write-Host "Testing basic registration (required fields only)..." -ForegroundColor Yellow
Write-Host "Request body: $body" -ForegroundColor Cyan

try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/register" -Method POST -Headers $headers -Body $body
    Write-Host "✓ Basic registration successful!" -ForegroundColor Green
    Write-Host "Response: $($response.Content)" -ForegroundColor Green
    Write-Host "Basic registration is working!" -ForegroundColor Green
} catch {
    Write-Host "✗ Basic registration failed!" -ForegroundColor Red
    Write-Host "Status: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response Body: $responseBody" -ForegroundColor Red
    }
} 