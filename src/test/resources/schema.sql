-- H2 Database Schema for Testing
-- This file is automatically executed by Spring Boot in test profile

-- Create persistent_logins table for remember-me functionality
-- (Only created if it doesn't exist)
CREATE TABLE IF NOT EXISTS persistent_logins (
    username  VARCHAR(64)  NOT NULL,
    series    VARCHAR(64)  NOT NULL PRIMARY KEY,
    token     VARCHAR(64)  NOT NULL,
    last_used TIMESTAMP    NOT NULL
);
