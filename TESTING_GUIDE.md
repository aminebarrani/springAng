# Authentication Testing Guide

## Prerequisites

1. **Install Java 17**
   - Go to https://adoptium.net/
   - Download Eclipse Temurin JDK 17 for Windows
   - Run the installer

2. **Set up Environment Variables**
   - Run the setup script: `.\setup_java.ps1`
   - Or manually set JAVA_HOME environment variable

3. **Start the Application**
   ```powershell
   ./mvnw spring-boot:run
   ```

## Testing the Authentication System

### 1. Test Authentication Endpoints

#### A. Test Auth Endpoint (No Authentication Required)
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/auth/test" -Method GET
```
**Expected Result**: `200 OK` with message "Auth endpoint is working!"

#### B. Register a New User
```powershell
$registerBody = @{
    username = "newuser"
    password = "password123"
    email = "newuser@example.com"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/auth/register" -Method POST -Body $registerBody -ContentType "application/json"
```
**Expected Result**: `200 OK` with JWT token, username, and role

#### C. Login with Existing User
```powershell
$loginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
$loginData = $response.Content | ConvertFrom-Json
$token = $loginData.token
```
**Expected Result**: `200 OK` with JWT token

### 2. Test Protected Endpoints

#### A. Access Protected Endpoint Without Token (Should Fail)
```powershell
try {
    Invoke-WebRequest -Uri "http://localhost:8080/api/personnes" -Method GET
} catch {
    Write-Host "Correctly blocked: $($_.Exception.Message)"
}
```
**Expected Result**: `401 Unauthorized`

#### B. Access Protected Endpoint With Token (Should Succeed)
```powershell
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

Invoke-WebRequest -Uri "http://localhost:8080/api/personnes" -Method GET -Headers $headers
```
**Expected Result**: `200 OK` with list of personnes

### 3. Test ToDoList Endpoint (Your Original Issue)

#### A. Create ToDoList Item with Authentication
```powershell
$todoBody = @{
    descrip = "Acheter du lait"
    personne = @{
        idPersonne = 2
    }
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://localhost:8080/api/todolist" -Method POST -Body $todoBody -Headers $headers
```
**Expected Result**: `200 OK` with created ToDoList item

#### B. Get All ToDoList Items
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/todolist" -Method GET -Headers $headers
```
**Expected Result**: `200 OK` with list of ToDoList items

## Pre-created Test Users

The application automatically creates these users on startup:

1. **Admin User**
   - Username: `admin`
   - Password: `admin123`
   - Role: `ADMIN`

2. **Regular User**
   - Username: `user`
   - Password: `user123`
   - Role: `USER`

## Quick Test Scripts

### Run All Tests
```powershell
.\test_auth.ps1
```

### Test ToDoList Specifically
```powershell
.\test_todo.ps1
```

## Troubleshooting

### Common Issues

1. **JAVA_HOME not set**
   - Run `.\setup_java.ps1`
   - Or manually set JAVA_HOME environment variable

2. **Application won't start**
   - Check if MySQL is running on localhost:3306
   - Check if database `my_company_db` exists
   - Check application logs for errors

3. **401 Unauthorized errors**
   - Make sure you're including the JWT token in Authorization header
   - Format: `Bearer <your-jwt-token>`
   - Check if token is expired (tokens expire after 24 hours)

4. **500 Internal Server Error**
   - Check application logs for detailed error messages
   - Verify database connection
   - Check if all required data exists (departments, personnes)

### Database Setup

Make sure you have:
1. MySQL running on localhost:3306
2. Database `my_company_db` created
3. User `root` with no password (or update application.properties)

```sql
CREATE DATABASE my_company_db;
```

## API Endpoints Summary

### Public Endpoints (No Authentication Required)
- `GET /api/auth/test` - Test auth endpoint
- `POST /api/auth/login` - Login
- `POST /api/auth/register` - Register

### Protected Endpoints (Authentication Required)
- `GET /api/personnes` - Get all personnes
- `POST /api/personnes` - Create personne
- `GET /api/personnes/{id}` - Get personne by ID
- `PUT /api/personnes/{id}` - Update personne
- `DELETE /api/personnes/{id}` - Delete personne
- `GET /api/todolist` - Get all ToDoList items
- `POST /api/todolist` - Create ToDoList item
- `GET /api/todolist/{id}` - Get ToDoList item by ID
- `PUT /api/todolist/{id}` - Update ToDoList item
- `DELETE /api/todolist/{id}` - Delete ToDoList item
- `GET /api/departments` - Get all departments
- `POST /api/departments` - Create department
- And all other CRUD operations for departments 