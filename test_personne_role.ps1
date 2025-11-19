# Test script for PERSONNE role functionality
# This script demonstrates how to create a user with PERSONNE role and test todo access

$baseUrl = "http://localhost:8080/api"

Write-Host "=== Testing PERSONNE Role Functionality ===" -ForegroundColor Green

# Step 1: Create a test personne (if needed)
Write-Host "`n1. Creating a test personne..." -ForegroundColor Yellow
$personneData = @{
    nom = "TestPersonne"
    prenom = "John"
    adresse = "123 Test Street"
    sex = "M"
    department = @{
        idDept = 1
    }
}

$personneResponse = Invoke-RestMethod -Uri "$baseUrl/personne" -Method POST -Body ($personneData | ConvertTo-Json) -ContentType "application/json"
$personneId = $personneResponse.idPersonne
Write-Host "Created personne with ID: $personneId" -ForegroundColor Green

# Step 2: Register a user with PERSONNE role linked to the personne
Write-Host "`n2. Registering user with PERSONNE role..." -ForegroundColor Yellow
$registerData = @{
    username = "testpersonne"
    password = "password123"
    email = "testpersonne@example.com"
    role = "PERSONNE"
    phoneNumber = "1234567890"
    firstname = "John"
    lastname = "Doe"
    image = ""
    personneId = $personneId
}

$registerResponse = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method POST -Body ($registerData | ConvertTo-Json) -ContentType "application/json"
$token = $registerResponse.token
Write-Host "Registered user with token: $($token.Substring(0, 20))..." -ForegroundColor Green

# Step 3: Create a todo for the personne
Write-Host "`n3. Creating a todo for the personne..." -ForegroundColor Yellow
$todoData = @{
    personne = @{
        idPersonne = $personneId
    }
    descrip = "Test todo for personne"
    datebeb = "2024-01-01"
    datefin = "2024-12-31"
    isChecked = $false
}

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

$todoResponse = Invoke-RestMethod -Uri "$baseUrl/todolist" -Method POST -Body ($todoData | ConvertTo-Json) -Headers $headers
Write-Host "Created todo with ID: $($todoResponse.idTache)" -ForegroundColor Green

# Step 4: Test getting my todos (should work for PERSONNE role)
Write-Host "`n4. Testing 'my-todos' endpoint..." -ForegroundColor Yellow
$myTodosResponse = Invoke-RestMethod -Uri "$baseUrl/todolist/my-todos" -Method GET -Headers $headers
Write-Host "Found $($myTodosResponse.Count) todos for the current user" -ForegroundColor Green

# Step 5: Test getting all todos (should fail for PERSONNE role)
Write-Host "`n5. Testing 'get all todos' endpoint (should fail for PERSONNE role)..." -ForegroundColor Yellow
try {
    $allTodosResponse = Invoke-RestMethod -Uri "$baseUrl/todolist" -Method GET -Headers $headers
    Write-Host "WARNING: PERSONNE role was able to access all todos - this should not happen!" -ForegroundColor Red
} catch {
    Write-Host "SUCCESS: PERSONNE role correctly denied access to all todos" -ForegroundColor Green
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Gray
}

# Step 6: Test getting todos by personne ID (should fail for PERSONNE role)
Write-Host "`n6. Testing 'get todos by personne ID' endpoint (should fail for PERSONNE role)..." -ForegroundColor Yellow
try {
    $personneTodosResponse = Invoke-RestMethod -Uri "$baseUrl/todolist/personne/$personneId" -Method GET -Headers $headers
    Write-Host "WARNING: PERSONNE role was able to access todos by personne ID - this should not happen!" -ForegroundColor Red
} catch {
    Write-Host "SUCCESS: PERSONNE role correctly denied access to todos by personne ID" -ForegroundColor Green
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Gray
}

# Step 7: Test updating own todo
Write-Host "`n7. Testing updating own todo..." -ForegroundColor Yellow
$updateData = @{
    descrip = "Updated test todo for personne"
    datebeb = "2024-01-01"
    datefin = "2024-12-31"
    isChecked = $true
}

$updateResponse = Invoke-RestMethod -Uri "$baseUrl/todolist/$($todoResponse.idTache)" -Method PUT -Body ($updateData | ConvertTo-Json) -Headers $headers
Write-Host "Successfully updated own todo" -ForegroundColor Green

# Step 8: Test deleting own todo
Write-Host "`n8. Testing deleting own todo..." -ForegroundColor Yellow
$deleteResponse = Invoke-RestMethod -Uri "$baseUrl/todolist/$($todoResponse.idTache)" -Method DELETE -Headers $headers
Write-Host "Successfully deleted own todo" -ForegroundColor Green

Write-Host "`n=== PERSONNE Role Test Completed Successfully ===" -ForegroundColor Green 