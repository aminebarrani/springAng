# Simple Authentication Test Script
Write-Host "Simple Authentication Test" -ForegroundColor Green
Write-Host "=========================" -ForegroundColor Green

# Step 1: Test if application is running
Write-Host "`nStep 1: Testing if application is running..." -ForegroundColor Yellow
try {
    $testResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/test" -Method GET
    Write-Host "✓ Application is running!" -ForegroundColor Green
    Write-Host "Response: $($testResponse.Content)" -ForegroundColor Cyan
} catch {
    Write-Host "✗ Application is not running!" -ForegroundColor Red
    Write-Host "Please start the application with: ./mvnw spring-boot:run" -ForegroundColor Yellow
    exit
}

# Step 2: Login with admin user
Write-Host "`nStep 2: Logging in with admin user..." -ForegroundColor Yellow
$loginBody = @{
    username = "admin"
    password = "admin123"
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
    exit
}

# Step 3: Test protected endpoint with token
Write-Host "`nStep 3: Testing protected endpoint with token..." -ForegroundColor Yellow
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

try {
    $protectedResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/personnes" -Method GET -Headers $headers
    Write-Host "✓ Protected endpoint access successful!" -ForegroundColor Green
    Write-Host "Status: $($protectedResponse.StatusCode)" -ForegroundColor Cyan
} catch {
    Write-Host "✗ Protected endpoint access failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Step 4: Test ToDoList creation (your original issue)
Write-Host "`nStep 4: Testing ToDoList creation..." -ForegroundColor Yellow
$todoBody = @{
    descrip = "Acheter du lait"
    personne = @{
        idPersonne = 2
    }
} | ConvertTo-Json

try {
    $todoResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/todolist" -Method POST -Body $todoBody -Headers $headers
    Write-Host "✓ ToDoList creation successful!" -ForegroundColor Green
    Write-Host "Response: $($todoResponse.Content)" -ForegroundColor Cyan
} catch {
    Write-Host "✗ ToDoList creation failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
}

# Step 5: Test without token (should fail)
Write-Host "`nStep 5: Testing without token (should fail)..." -ForegroundColor Yellow
try {
    $noTokenResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/personnes" -Method GET
    Write-Host "✗ Should have failed!" -ForegroundColor Red
} catch {
    Write-Host "✓ Correctly blocked access without token!" -ForegroundColor Green
}

Write-Host "`nTest completed!" -ForegroundColor Green 