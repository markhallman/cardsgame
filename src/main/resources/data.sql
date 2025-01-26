--File for populating data for embedded database for testing
-- (both passwords are hashed "test")
INSERT INTO users (username, password)
SELECT 'mark', '$2y$12$xrTjV4nknmKlg5f2vfyffu4.13zuK4bFNPuepFpHU9LAXy2lrDfZ.'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'mark');

INSERT INTO users (username, password)
SELECT 'devon', '$2y$12$w5czN9I/cpDG5GXqcnOKtup1QVe4U8YanIrN8rCehBxBxpCGlJPTG'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'devon');