-- Add personne_id column to users table
ALTER TABLE users ADD COLUMN personne_id BIGINT;

-- Add foreign key constraint
ALTER TABLE users ADD CONSTRAINT fk_users_personne 
    FOREIGN KEY (personne_id) REFERENCES personne(id_personne);

-- Create index for better performance
CREATE INDEX idx_users_personne_id ON users(personne_id);

-- Optional: Update existing users to have PERSONNE role if they are linked to a personne
-- This is just an example - you would need to manually link specific users to personnes
-- UPDATE users SET role = 'PERSONNE' WHERE personne_id IS NOT NULL AND role = 'USER'; 