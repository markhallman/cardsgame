--File for populating data for embedded database for testing
-- (both passwords are hashed "test")
INSERT INTO users (username, password, email)
SELECT 'mark', '$2y$12$xrTjV4nknmKlg5f2vfyffu4.13zuK4bFNPuepFpHU9LAXy2lrDfZ.', 'mw-hallman@sbcglobal.net'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'mark');

INSERT INTO users (username, password, email)
SELECT 'devon', '$2y$12$w5czN9I/cpDG5GXqcnOKtup1QVe4U8YanIrN8rCehBxBxpCGlJPTG', 'markwhallman@gmail.com'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'devon');