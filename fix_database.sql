-- Fix the users table schema to allow NULL values for optional fields
USE my_company_db;

-- Make optional fields nullable
ALTER TABLE users MODIFY COLUMN phoneNumber VARCHAR(255) NULL;
ALTER TABLE users MODIFY COLUMN firstname VARCHAR(255) NULL;
ALTER TABLE users MODIFY COLUMN lastname VARCHAR(255) NULL;
ALTER TABLE users MODIFY COLUMN image LONGTEXT;

-- Verify the changes
DESCRIBE users; 