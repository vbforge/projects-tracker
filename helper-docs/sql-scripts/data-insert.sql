-- =====================================================
-- PROJECT TRACKER - DATA INSERTION SCRIPT
-- =====================================================
-- Run this script AFTER Hibernate creates the tables
-- This will populate your database with tags and your 25 projects

-- =====================================================
-- SAMPLE DATA - TAGS
-- =====================================================
INSERT INTO tags (name, color, description, created_date, updated_at) VALUES
('Spring Boot', '#6db33f', 'Spring Boot framework projects', NOW(), NOW()),
('MySQL', '#00758f', 'Projects using MySQL database', NOW(), NOW()),
('Kafka', '#231f20', 'Apache Kafka related projects', NOW(), NOW()),
('Docker', '#2496ed', 'Dockerized applications', NOW(), NOW()),
('Tutorial', '#ffd700', 'Tutorial and learning projects', NOW(), NOW()),
('Learning', '#ff6b6b', 'Personal learning projects', NOW(), NOW()),
('Production', '#28a745', 'Production-ready projects', NOW(), NOW()),
('REST API', '#61dafb', 'RESTful API projects', NOW(), NOW()),
('Microservices', '#ff6347', 'Microservices architecture', NOW(), NOW()),
('Testing', '#9b59b6', 'Projects with comprehensive testing', NOW(), NOW()),
('GUI', '#3498db', 'Projects with graphical user interface', NOW(), NOW()),
('CLI', '#95a5a6', 'Command-line interface applications', NOW(), NOW()),
('Multi-module', '#e67e22', 'Multi-module Maven/Gradle projects', NOW(), NOW()),
('Documentation', '#34495e', 'Documentation and writing projects', NOW(), NOW()),
('Game', '#e91e63', 'Game development projects', NOW(), NOW()),
('JPA', '#4a90e2', 'JPA/Hibernate projects', NOW(), NOW()),
('Thymeleaf', '#005f0f', 'Thymeleaf template engine', NOW(), NOW()),
('Collections', '#8b4513', 'Java Collections Framework', NOW(), NOW()),
('Concurrency', '#dc143c', 'Threading and concurrency', NOW(), NOW()),
('AOP', '#4169e1', 'Aspect-Oriented Programming', NOW(), NOW())
ON DUPLICATE KEY UPDATE name=name;

-- =====================================================
-- SAMPLE DATA - YOUR 25 PROJECTS
-- =====================================================
INSERT INTO projects (title, description, status, on_github, github_url, what_todo, created_date, last_worked_on) VALUES

('football-stats', 
 'Football Statistics App with Spring Boot and MySQL', 
 'DONE', 
 TRUE, 
 'https://github.com/yourusername/football-stats', 
 NULL, 
 '2024-01-15 10:00:00', 
 '2024-01-20 15:30:00'),

('Fewster', 
 'Fewster - SpringBoot App to convert original URL into shortest one and vise-versa', 
 'DONE', 
 TRUE,
 'https://github.com/yourusername/Fewster', 
 NULL, 
 '2024-02-01 09:00:00', 
 '2024-02-15 14:00:00'),

('LeetCode-Problems-Solutions', 
 'Leet Code Problems and Solutions, + other algorithms', 
 'IN_PROGRESS', 
 TRUE,
 'https://github.com/yourusername/LeetCode-Problems-Solutions', 
 'Add more dynamic programming solutions, Complete graph algorithms section', 
 '2023-11-10 08:00:00', 
 '2026-01-25 16:00:00'),

('Dual-DB-SpringBoot-App', 
 'A Spring Boot REST application that demonstrates data transfer between two separate MySQL databases using Docker + testing with Jmeter', 
 'DONE', 
 TRUE,
 'https://github.com/yourusername/Dual-DB-SpringBoot-App', 
 NULL, 
 '2024-03-05 10:30:00', 
 '2024-03-20 17:00:00'),

('Java-Core', 
 'Multi-modular repository exploring fundamental Java Core principles. Each module covers concrete concept or task with description of what it does', 
 'IN_PROGRESS', 
 TRUE,
 'https://github.com/yourusername/Java-Core', 
 'Add more modules on concurrency, Complete reflection module', 
 '2023-10-01 09:00:00', 
 '2026-01-28 11:00:00'),

('Spring6-SpringBoot3', 
 'This repository contains projects related to the Spring 6 and Spring Boot 3 course', 
 'DONE', 
 TRUE,
 'https://github.com/yourusername/Spring6-SpringBoot3', 
 NULL, 
 '2023-12-01 10:00:00', 
 '2024-01-30 16:00:00'),

('Collections', 
 'A deep dive into the Java Collections Framework, including interfaces (List, Set, Map) and utility classes (Collections, Arrays). Java Collections Framework (JCF) Playground', 
 'DONE', 
 TRUE,
 'https://github.com/yourusername/Collections', 
 NULL, 
 '2024-01-05 09:00:00', 
 '2024-02-10 15:00:00'),

('Threading-and-Multithreading', 
 'A collection of Java projects focused on threading and multithreading techniques', 
 'IN_PROGRESS', 
 TRUE,
 'https://github.com/yourusername/Threading-and-Multithreading', 
 'Add examples with virtual threads (Java 21), Complete thread pool examples', 
 '2024-02-15 10:00:00', 
 '2026-01-26 14:00:00'),

('AOP-Spring-basics-tutorial', 
 'Complete Spring AOP learning project with multiple modules covering different aspects', 
 'DONE', 
 TRUE,
 'https://github.com/yourusername/AOP-Spring-basics-tutorial', 
 NULL, 
 '2024-03-01 09:30:00', 
 '2024-04-01 16:30:00'),

('java-mastery-roadmap-book', 
 'Writing strategy: one chapter per week, each chapter 15-20 pages, include code examples and exercises', 
 'NOT_STARTED', 
 TRUE,
 'https://github.com/yourusername/java-mastery-roadmap-book', 
 'Create detailed outline, Start chapter 1: Java Fundamentals, Set up book structure', 
 '2024-11-10 10:00:00', 
 '2024-11-10 10:00:00'),

('java-learning-journey', 
 'This repository documents my journey through a 100-module curriculum designed to take me from Java fundamentals to production-ready enterprise development', 
 'IN_PROGRESS', 
 TRUE,
 'https://github.com/yourusername/java-learning-journey', 
 'Complete modules 45-50, Review and update earlier modules', 
 '2023-09-01 08:00:00', 
 '2026-01-27 17:00:00'),

('eng-tracker', 
 'English B1→C1 Master Learning Plan; 90-Day Intensive Course with IT Specialization', 
 'IN_PROGRESS', 
 TRUE,
 'https://github.com/yourusername/eng-tracker', 
 'Continue with week 5 exercises, Practice technical vocabulary', 
 '2024-10-01 09:00:00', 
 '2026-01-20 12:00:00'),

('kafka-enhanced-spring-boot-production', 
 'Apache Kafka with Spring Boot, 5 base phases (+ phase 6 optional) project production-grade progression: Kafka fundamentals, design patterns, observability, transactions, and automated testing', 
 'IN_PROGRESS', 
 TRUE,
 'https://github.com/yourusername/kafka-enhanced-spring-boot-production', 
 'Implement phase 5: automated testing, Add monitoring dashboards', 
 '2024-12-01 10:00:00', 
 '2026-01-28 18:00:00'),

('kafka-enhanced-spring-boot-docker-production', 
 'A comprehensive multi-module Spring Boot project demonstrating Kafka integration from basics to production-ready features: producer/consumer applications, fully Dockerized Kafka broker (KRaft mode), Conductor UI on localhost:8080, and PostgreSQL', 
 'IN_PROGRESS', 
 TRUE,
 'https://github.com/yourusername/kafka-enhanced-spring-boot-docker-production', 
 'Add Conductor UI configuration, Improve Docker compose setup', 
 '2024-12-15 11:00:00', 
 '2026-01-28 19:00:00'),

('concierge-scheduler', 
 'Spring Boot App for scheduling working shifts as a calendar, with admin account and perform basic analytics/stats', 
 'IN_PROGRESS', 
 FALSE,
 NULL, 
 'Implement analytics dashboard, Add unit tests, Deploy to production', 
 '2024-05-01 09:00:00', 
 '2026-01-15 13:00:00'),

('the-app-equation', 
 'Java app to resolving simple and advance equation calculation, dao pattern with MySQL', 
 'DONE', 
 FALSE,
 NULL, 
 NULL, 
 '2023-08-15 10:00:00', 
 '2023-09-20 15:00:00'),

('twilio-sms-demo', 
 'twilio-sms-demo Spring Boot Twilio SMS Application with thymeleaf view', 
 'DONE', 
 TRUE,
 'https://github.com/yourusername/twilio-sms-demo', 
 NULL, 
 '2024-04-10 10:30:00', 
 '2024-04-25 16:00:00'),

('Uvocorp-1-Buritto-ordering-system', 
 'Simple java project with builder pattern and oop, overloaded constructor', 
 'DONE', 
 TRUE,
 'https://github.com/yourusername/Uvocorp-1-Buritto-ordering-system', 
 NULL, 
 '2023-06-01 09:00:00', 
 '2023-06-15 14:00:00'),

('Uvocorp-2-codename1_game_World', 
 'Java app with third-party depend codename1 (game)', 
 'DONE', 
 TRUE,
 'https://github.com/yourusername/Uvocorp-2-codename1_game_World', 
 NULL, 
 '2023-06-20 10:00:00', 
 '2023-07-05 15:00:00'),

('Uvocorp-3-Encrypting_Mitigation', 
 'Simple java project with oop, console i/o, files, scanner, PrintWriter', 
 'DONE', 
 TRUE,
 'https://github.com/yourusername/Uvocorp-3-Encrypting_Mitigation', 
 NULL, 
 '2023-07-10 09:30:00', 
 '2023-07-20 14:30:00'),

('Uvocorp-4-Least_Recently_Used_Cache', 
 'LRUC pattern resolved, and example with threads', 
 'DONE', 
 TRUE,
 'https://github.com/yourusername/Uvocorp-4-Least_Recently_Used_Cache', 
 NULL, 
 '2023-07-25 10:00:00', 
 '2023-08-10 16:00:00'),

('Uvocorp-5-Dietary Survey Form_ProjectGUI', 
 'Java app with Form GUI, user enter data in the form, and it saved in csv file directly', 
 'DONE', 
 TRUE,
 'https://github.com/yourusername/Uvocorp-5-Dietary Survey Form_ProjectGUI', 
 NULL, 
 '2023-08-15 09:00:00', 
 '2023-09-01 15:00:00'),

('Disk-Analyzer', 
 'Project for making analise of current(chosen) disc (folder) java app with Form GUI', 
 'DONE', 
 FALSE,
 NULL, 
 NULL, 
 '2023-05-10 10:00:00', 
 '2023-05-25 16:00:00'),

('formula_one', 
 'Practice to design database, creating an ER diagram by defining the entities, their attributes and showing their relationships', 
 'DONE', 
 TRUE,
 'https://github.com/yourusername/formula_one', 
 NULL, 
 '2024-01-10 09:00:00', 
 '2024-01-25 15:00:00'),

('java-professional-itvdn-modules', 
 'The "Java Advanced" course is designed for students with basic knowledge of the Java language', 
 'IN_PROGRESS', 
 TRUE,
 'https://github.com/yourusername/java-professional-itvdn-modules', 
 'Complete modules on reflection and annotations, Finish generics section', 
 '2023-11-01 08:00:00', 
 '2026-01-22 17:00:00')

ON DUPLICATE KEY UPDATE title=title;

-- =====================================================
-- PROJECT_TAGS RELATIONSHIPS
-- =====================================================
-- Assign tags to projects

INSERT INTO project_tags (project_id, tag_id) 
SELECT p.id, t.id FROM projects p, tags t 
WHERE p.title = 'football-stats' AND t.name IN ('Spring Boot', 'MySQL');

INSERT INTO project_tags (project_id, tag_id) 
SELECT p.id, t.id FROM projects p, tags t 
WHERE p.title = 'Fewster' AND t.name IN ('Spring Boot', 'REST API');

INSERT INTO project_tags (project_id, tag_id) 
SELECT p.id, t.id FROM projects p, tags t 
WHERE p.title = 'LeetCode-Problems-Solutions' AND t.name IN ('Learning', 'CLI');

INSERT INTO project_tags (project_id, tag_id) 
SELECT p.id, t.id FROM projects p, tags t 
WHERE p.title = 'Dual-DB-SpringBoot-App' AND t.name IN ('Spring Boot', 'MySQL', 'Docker', 'REST API', 'Testing');

INSERT INTO project_tags (project_id, tag_id) 
SELECT p.id, t.id FROM projects p, tags t 
WHERE p.title = 'Java-Core' AND t.name IN ('Learning', 'Multi-module');

INSERT INTO project_tags (project_id, tag_id) 
SELECT p.id, t.id FROM projects p, tags t 
WHERE p.title = 'Spring6-SpringBoot3' AND t.name IN ('Spring Boot', 'Tutorial');

INSERT INTO project_tags (project_id, tag_id) 
SELECT p.id, t.id FROM projects p, tags t 
WHERE p.title = 'Collections' AND t.name IN ('Learning', 'Tutorial', 'Collections');

INSERT INTO project_tags (project_id, tag_id) 
SELECT p.id, t.id FROM projects p, tags t 
WHERE p.title = 'Threading-and-Multithreading' AND t.name IN ('Learning', 'Tutorial', 'Concurrency');

INSERT INTO project_tags (project_id, tag_id) 
SELECT p.id, t.id FROM projects p, tags t 
WHERE p.title = 'AOP-Spring-basics-tutorial' AND t.name IN ('Spring Boot', 'Tutorial', 'Multi-module', 'AOP');

INSERT INTO project_tags (project_id, tag_id) 
SELECT p.id, t.id FROM projects p, tags t 
WHERE p.title = 'java-mastery-roadmap-book' AND t.name IN ('Documentation', 'Learning');

INSERT INTO project_tags (project_id, tag_id) 
SELECT p.id, t.id FROM projects p, tags t 
WHERE p.title = 'java-learning-journey' AND t.name IN ('Learning', 'Multi-module');

INSERT INTO project_tags (project_id, tag_id) 
SELECT p.id, t.id FROM projects p, tags t 
WHERE p.title = 'eng-tracker' AND t.name IN ('Learning', 'Documentation');

INSERT INTO project_tags (project_id, tag_id) 
SELECT p.id, t.id FROM projects p, tags t 
WHERE p.title = 'kafka-enhanced-spring-boot-production' AND t.name IN ('Spring Boot', 'Kafka', 'Production', 'Multi-module');

INSERT INTO project_tags (project_id, tag_id) 
SELECT p.id, t.id FROM projects p, tags t 
WHERE p.title = 'kafka-enhanced-spring-boot-docker-production' AND t.name IN ('Spring Boot', 'Kafka', 'Docker', 'Production', 'Multi-module');

INSERT INTO project_tags (project_id, tag_id) 
SELECT p.id, t.id FROM projects p, tags t 
WHERE p.title = 'concierge-scheduler' AND t.name IN ('Spring Boot', 'MySQL', 'GUI');

INSERT INTO project_tags (project_id, tag_id) 
SELECT p.id, t.id FROM projects p, tags t 
WHERE p.title = 'the-app-equation' AND t.name IN ('MySQL', 'CLI');

INSERT INTO project_tags (project_id, tag_id) 
SELECT p.id, t.id FROM projects p, tags t 
WHERE p.title = 'twilio-sms-demo' AND t.name IN ('Spring Boot', 'GUI');

INSERT INTO project_tags (project_id, tag_id) 
SELECT p.id, t.id FROM projects p, tags t 
WHERE p.title = 'Uvocorp-1-Buritto-ordering-system' AND t.name IN ('Tutorial');

INSERT INTO project_tags (project_id, tag_id) 
SELECT p.id, t.id FROM projects p, tags t 
WHERE p.title = 'Uvocorp-2-codename1_game_World' AND t.name IN ('Game', 'GUI');

INSERT INTO project_tags (project_id, tag_id) 
SELECT p.id, t.id FROM projects p, tags t 
WHERE p.title = 'Uvocorp-3-Encrypting_Mitigation' AND t.name IN ('CLI');

INSERT INTO project_tags (project_id, tag_id) 
SELECT p.id, t.id FROM projects p, tags t 
WHERE p.title = 'Uvocorp-4-Least_Recently_Used_Cache' AND t.name IN ('Tutorial', 'Learning', 'Concurrency');

INSERT INTO project_tags (project_id, tag_id) 
SELECT p.id, t.id FROM projects p, tags t 
WHERE p.title = 'Uvocorp-5-Dietary Survey Form_ProjectGUI' AND t.name IN ('GUI');

INSERT INTO project_tags (project_id, tag_id) 
SELECT p.id, t.id FROM projects p, tags t 
WHERE p.title = 'Disk-Analyzer' AND t.name IN ('GUI');

INSERT INTO project_tags (project_id, tag_id) 
SELECT p.id, t.id FROM projects p, tags t 
WHERE p.title = 'formula_one' AND t.name IN ('MySQL', 'Learning');

INSERT INTO project_tags (project_id, tag_id) 
SELECT p.id, t.id FROM projects p, tags t 
WHERE p.title = 'java-professional-itvdn-modules' AND t.name IN ('Tutorial', 'Learning', 'Multi-module');
