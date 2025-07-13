# Test ToDoList with Authentication

Write-Host "Testing ToDoList with Authentication..." -ForegroundColor Green

# Step 1: Login
Write-Host "`n1. Logging in..." -ForegroundColor Yellow
$loginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    $loginData = $loginResponse.Content | ConvertFrom-Json
    $token = $loginData.token
    Write-Host "Login successful! Token: $($token.Substring(0, 20))..." -ForegroundColor Green
} catch {
    Write-Host "Login failed: $($_.Exception.Message)" -ForegroundColor Red
    exit
}

# Step 2: Create ToDoList item
Write-Host "`n2. Creating ToDoList item..." -ForegroundColor Yellow
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$todoBody = @{
    descrip = "Acheter du lait"
    personne = @{
        idPersonne = 2
    }
} | ConvertTo-Json

try {
    $todoResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/todolist" -Method POST -Body $todoBody -Headers $headers
    Write-Host "ToDoList creation successful!" -ForegroundColor Green
    Write-Host "Response: $($todoResponse.Content)" -ForegroundColor Cyan
} catch {
    Write-Host "ToDoList creation failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
}

Write-Host "`nTest completed!" -ForegroundColor Green 