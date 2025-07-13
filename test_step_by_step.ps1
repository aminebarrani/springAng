# Step-by-Step Authentication Test
Write-Host "Step-by-Step Authentication Test" -ForegroundColor Green
Write-Host "===============================" -ForegroundColor Green

# Step 1: Check if application is running
Write-Host "`nStep 1: Checking if application is running..." -ForegroundColor Yellow
try {
    $testResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/test" -Method GET
    Write-Host "✓ Application is running!" -ForegroundColor Green
} catch {
    Write-Host "✗ Application is not running!" -ForegroundColor Red
    Write-Host "Please start the application with: ./mvnw spring-boot:run" -ForegroundColor Yellow
    exit
}

# Step 2: Try to register a new user first
Write-Host "`nStep 2: Registering a new test user..." -ForegroundColor Yellow
$registerBody = @{
    username = "testuser2"
    password = "testpass123"
    email = "testuser2@example.com"
} | ConvertTo-Json

try {
    $registerResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/register" -Method POST -Body $registerBody -ContentType "application/json"
    Write-Host "✓ Registration successful!" -ForegroundColor Green
    $registerData = $registerResponse.Content | ConvertFrom-Json
    $token = $registerData.token
    Write-Host "Token: $($token.Substring(0, 20))..." -ForegroundColor Cyan
} catch {
    Write-Host "✗ Registration failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Trying login instead..." -ForegroundColor Yellow
    
    # Try login with existing user
    $loginBody = @{
        username = "testuser"
        password = "testpass123"
    } | ConvertTo-Json
    
    try {
        $loginResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
        Write-Host "✓ Login successful!" -ForegroundColor Green
        $loginData = $loginResponse.Content | ConvertFrom-Json
        $token = $loginData.token
        Write-Host "Token: $($token.Substring(0, 20))..." -ForegroundColor Cyan
    } catch {
        Write-Host "✗ Login also failed: $($_.Exception.Message)" -ForegroundColor Red
        exit
    }
}

# Step 3: Test the token with debug endpoint
Write-Host "`nStep 3: Testing token with debug endpoint..." -ForegroundColor Yellow
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
    Write-Host "This means the JWT token is not being validated correctly" -ForegroundColor Yellow
}

# Step 4: Test personnes endpoint
Write-Host "`nStep 4: Testing personnes endpoint..." -ForegroundColor Yellow
try {
    $personnesResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/personnes" -Method GET -Headers $headers
    Write-Host "✓ Personnes endpoint successful!" -ForegroundColor Green
    Write-Host "Status: $($personnesResponse.StatusCode)" -ForegroundColor Cyan
} catch {
    Write-Host "✗ Personnes endpoint failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
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

Write-Host "`nTest completed!" -ForegroundColor Green
Write-Host "Check the application console for JWT filter debug messages" -ForegroundColor Yellow 