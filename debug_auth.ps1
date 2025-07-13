# Debug Authentication Script
Write-Host "Debug Authentication Test" -ForegroundColor Green
Write-Host "=========================" -ForegroundColor Green

# Step 1: Test if application is running
Write-Host "`nStep 1: Testing if application is running..." -ForegroundColor Yellow
try {
    $testResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/test" -Method GET
    Write-Host "✓ Application is running!" -ForegroundColor Green
    Write-Host "Response: $($testResponse.Content)" -ForegroundColor Cyan
} catch {
    Write-Host "✗ Application is not running!" -ForegroundColor Red
    Write-Host "Please start the application first" -ForegroundColor Yellow
    exit
}

# Step 2: Login with testuser (the one you used in Postman)
Write-Host "`nStep 2: Logging in with testuser..." -ForegroundColor Yellow
$loginBody = @{
    username = "testuser"
    password = "testpass123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    $loginData = $loginResponse.Content | ConvertFrom-Json
    $token = $loginData.token
    Write-Host "✓ Login successful!" -ForegroundColor Green
    Write-Host "Username: $($loginData.username)" -ForegroundColor Cyan
    Write-Host "Role: $($loginData.role)" -ForegroundColor Cyan
    Write-Host "Token: $($token.Substring(0, 20))..." -ForegroundColor Cyan
} catch {
    Write-Host "✗ Login failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Response: $($_.Exception.Response.Content)" -ForegroundColor Red
    exit
}

# Step 3: Test debug endpoint with token
Write-Host "`nStep 3: Testing debug endpoint with token..." -ForegroundColor Yellow
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

try {
    $debugResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/debug" -Method GET -Headers $headers
    Write-Host "✓ Debug endpoint successful!" -ForegroundColor Green
    Write-Host "Response: $($debugResponse.Content)" -ForegroundColor Cyan
} catch {
    Write-Host "✗ Debug endpoint failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
}

# Step 4: Test personnes endpoint with token
Write-Host "`nStep 4: Testing personnes endpoint with token..." -ForegroundColor Yellow
try {
    $personnesResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/personnes" -Method GET -Headers $headers
    Write-Host "✓ Personnes endpoint successful!" -ForegroundColor Green
    Write-Host "Status: $($personnesResponse.StatusCode)" -ForegroundColor Cyan
    Write-Host "Response length: $($personnesResponse.Content.Length) characters" -ForegroundColor Cyan
} catch {
    Write-Host "✗ Personnes endpoint failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    Write-Host "Response: $($_.Exception.Response.Content)" -ForegroundColor Red
}

# Step 5: Test without token (should fail)
Write-Host "`nStep 5: Testing without token (should fail)..." -ForegroundColor Yellow
try {
    $noTokenResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/personnes" -Method GET
    Write-Host "✗ Should have failed!" -ForegroundColor Red
} catch {
    Write-Host "✓ Correctly blocked access without token!" -ForegroundColor Green
    Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Cyan
}

Write-Host "`nDebug test completed!" -ForegroundColor Green
Write-Host "Check the application console for JWT filter debug messages" -ForegroundColor Yellow 