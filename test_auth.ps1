# Test Authentication Endpoints

Write-Host "Testing Authentication System..." -ForegroundColor Green

# Test 1: Login with admin user
Write-Host "`n1. Testing login with admin user..." -ForegroundColor Yellow
$loginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    Write-Host "Login successful!" -ForegroundColor Green
    Write-Host "Response: $($loginResponse.Content)" -ForegroundColor Cyan
    
    # Extract token from response
    $loginData = $loginResponse.Content | ConvertFrom-Json
    $token = $loginData.token
    
    Write-Host "Token received: $($token.Substring(0, 20))..." -ForegroundColor Cyan
    
    # Test 2: Access protected endpoint with token
    Write-Host "`n2. Testing protected endpoint with token..." -ForegroundColor Yellow
    $headers = @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }
    
    $protectedResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/personnes" -Method GET -Headers $headers
    Write-Host "Protected endpoint access successful!" -ForegroundColor Green
    Write-Host "Response status: $($protectedResponse.StatusCode)" -ForegroundColor Cyan
    
    # Test 3: Try to access protected endpoint without token (should fail)
    Write-Host "`n3. Testing protected endpoint without token (should fail)..." -ForegroundColor Yellow
    try {
        $noTokenResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/personnes" -Method GET -ContentType "application/json"
        Write-Host "ERROR: Should have failed!" -ForegroundColor Red
    } catch {
        Write-Host "Correctly blocked access without token!" -ForegroundColor Green
        Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Cyan
    }
    
} catch {
    Write-Host "Login failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nAuthentication test completed!" -ForegroundColor Green 