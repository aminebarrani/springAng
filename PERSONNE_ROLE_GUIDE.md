# PERSONNE Role Implementation Guide

## Overview

This implementation adds a new `PERSONNE` role to the system, allowing personnes to access and manage their own todos while maintaining proper security boundaries.

## What's New

### 1. Entity Relationships
- **User-Personne Link**: Added a `OneToOne` relationship between `User` and `Personne` entities
- **Bidirectional Mapping**: Both entities can reference each other
- **Database Schema**: New `personne_id` column in the `users` table

### 2. New Role: PERSONNE
- **Role Name**: `PERSONNE`
- **Permissions**: Can only access and manage their own todos
- **Security**: Restricted access to admin-only endpoints

### 3. New API Endpoints

#### For PERSONNE Role:
- `GET /api/todolist/my-todos` - Get current user's todos
- `GET /api/todolist/{id}` - Get specific todo (if owner)
- `POST /api/todolist` - Create new todo (for themselves)
- `PUT /api/todolist/{id}` - Update todo (if owner)
- `DELETE /api/todolist/{id}` - Delete todo (if owner)

#### For ADMIN Role:
- All existing endpoints remain available
- Can access todos for any personne
- Can manage all todos in the system

## Database Changes

Run the following SQL script to update your database:

```sql
-- Add personne_id column to users table
ALTER TABLE users ADD COLUMN personne_id BIGINT;

-- Add foreign key constraint
ALTER TABLE users ADD CONSTRAINT fk_users_personne 
    FOREIGN KEY (personne_id) REFERENCES personne(id_personne);

-- Create index for better performance
CREATE INDEX idx_users_personne_id ON users(personne_id);
```

## How to Use

### 1. Register a User with PERSONNE Role

```json
POST /api/auth/register
{
    "username": "john_doe",
    "password": "password123",
    "email": "john@example.com",
    "role": "PERSONNE",
    "phoneNumber": "1234567890",
    "firstname": "John",
    "lastname": "Doe",
    "image": "",
    "personneId": 1
}
```

### 2. Login and Get Token

```json
POST /api/auth/login
{
    "username": "john_doe",
    "password": "password123"
}
```

### 3. Access Personal Todos

```bash
GET /api/todolist/my-todos
Authorization: Bearer <your-jwt-token>
```

### 4. Create Personal Todo

```json
POST /api/todolist
Authorization: Bearer <your-jwt-token>
{
    "personne": {
        "idPersonne": 1
    },
    "descrip": "Complete project documentation",
    "datebeb": "2024-01-01",
    "datefin": "2024-12-31",
    "isChecked": false
}
```

## Security Features

### 1. Role-Based Access Control
- **PERSONNE**: Can only access their own todos
- **ADMIN**: Can access all todos and manage the system

### 2. Ownership Validation
- Users can only modify todos they own
- Automatic validation on all CRUD operations
- Prevents unauthorized access to other users' todos

### 3. Endpoint Protection
- `/api/todolist/my-todos` - Requires PERSONNE or ADMIN role
- `/api/todolist/personne/{personneId}` - Admin only
- All other endpoints - Proper role validation

## Testing

Use the provided test script to verify functionality:

```powershell
.\test_personne_role.ps1
```

This script will:
1. Create a test personne
2. Register a user with PERSONNE role
3. Create and manage todos
4. Test security restrictions
5. Verify proper access control

## Code Changes Summary

### Modified Files:
1. **User.java** - Added Personne relationship
2. **Personne.java** - Added User relationship
3. **RegisterRequest.java** - Added personneId field
4. **AuthController.java** - Added personne linking logic
5. **ToDoListController.java** - Added role-based access control
6. **SecurityConfig.java** - Updated security rules

### New Files:
1. **add_personne_role.sql** - Database migration script
2. **test_personne_role.ps1** - Test script
3. **PERSONNE_ROLE_GUIDE.md** - This guide

## Best Practices

### 1. User Registration
- Always validate that the personne exists before linking
- Ensure proper role assignment based on personneId
- Validate that personneId is not already linked to another user

### 2. Security
- Always check ownership before allowing modifications
- Use proper authentication and authorization
- Validate input data thoroughly

### 3. Error Handling
- Provide clear error messages for unauthorized access
- Handle cases where personne is not found
- Validate relationships before operations

## Troubleshooting

### Common Issues:

1. **"Personne not found" error**
   - Ensure the personneId exists in the database
   - Check that the personne was created successfully

2. **"You can only access your own todos" error**
   - Verify the user is linked to the correct personne
   - Check that the todo belongs to the user's personne

3. **Database constraint violations**
   - Run the migration script to add the new column
   - Ensure foreign key constraints are properly set

4. **Authentication issues**
   - Verify JWT token is valid and not expired
   - Check that the user has the correct role

## Future Enhancements

Potential improvements for the PERSONNE role system:

1. **Department-based access**: Allow personnes to see todos from their department
2. **Team collaboration**: Enable sharing todos between team members
3. **Advanced permissions**: Granular permission system for different todo operations
4. **Audit logging**: Track who accessed and modified todos
5. **Bulk operations**: Allow admins to manage multiple todos efficiently 