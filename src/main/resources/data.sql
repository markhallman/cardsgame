--File for populating data for embedded database for testing
INSERT INTO users (username, password)
SELECT 'mark', 'test'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'mark');

INSERT INTO users (username, password)
SELECT 'devon', 'test'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'devon');