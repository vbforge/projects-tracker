use project_tracker;

-- insert tags
INSERT INTO tags (color, created_date, description, name, updated_at, user_id) VALUES
-- Denis (user_id = 1)
('#FF5733', NOW(), 'Spring related projects', 'Spring', NOW(), 1),
('#33B5FF', NOW(), 'Java core and advanced', 'Java', NOW(), 1),
('#28A745', NOW(), 'Frontend experiments', 'Frontend', NOW(), 1),
('#FFC107', NOW(), 'Security implementations', 'Security', NOW(), 1),
('#6F42C1', NOW(), 'Docker and DevOps', 'DevOps', NOW(), 1),
('#20C997', NOW(), 'Database design', 'Database', NOW(), 1),
('#E83E8C', NOW(), 'Testing projects', 'Testing', NOW(), 1),
('#17A2B8', NOW(), 'REST APIs', 'REST', NOW(), 1),
('#6610F2', NOW(), 'Microservices research', 'Microservices', NOW(), 1),
('#FD7E14', NOW(), 'Learning playground', 'Learning', NOW(), 1),

-- Vlad (user_id = 2)
('#FF5733', NOW(), 'Spring related projects', 'Spring', NOW(), 2),
('#33B5FF', NOW(), 'Java core and advanced', 'Java', NOW(), 2),
('#28A745', NOW(), 'Frontend experiments', 'Frontend', NOW(), 2),
('#FFC107', NOW(), 'Security implementations', 'Security', NOW(), 2),
('#6F42C1', NOW(), 'Docker and DevOps', 'DevOps', NOW(), 2),
('#20C997', NOW(), 'Database design', 'Database', NOW(), 2),
('#E83E8C', NOW(), 'Testing projects', 'Testing', NOW(), 2),
('#17A2B8', NOW(), 'REST APIs', 'REST', NOW(), 2),
('#6610F2', NOW(), 'Microservices research', 'Microservices', NOW(), 2),
('#FD7E14', NOW(), 'Learning playground', 'Learning', NOW(), 2);


-- insert projects
INSERT INTO projects
(created_date, description, github_url, last_worked_on, local_path, on_github, status, title, updated_at, what_todo, user_id)
VALUES
-- Denis projects (user_id = 1)
(NOW(), 'Spring Boot security project', 'https://github.com/denis/security-app', NOW(), '/projects/security-app', 1, 'IN_PROGRESS', 'Security App', NOW(), 'Add JWT refresh tokens', 1),
(NOW(), 'Project tracker app', NULL, NOW(), '/projects/tracker', 0, 'DONE', 'Project Tracker', NOW(), NULL, 1),
(NOW(), 'REST API demo', NULL, NOW(), '/projects/rest-api', 0, 'NOT_STARTED', 'REST API Demo', NOW(), 'Design endpoints', 1),
(NOW(), 'Docker experiments', NULL, NOW(), '/projects/docker-lab', 0, 'IN_PROGRESS', 'Docker Lab', NOW(), NULL, 1),
(NOW(), 'Microservices architecture', NULL, NOW(), '/projects/micro', 0, 'NOT_STARTED', 'Microservices App', NOW(), NULL, 1),
(NOW(), 'Thymeleaf UI project', NULL, NOW(), '/projects/ui', 0, 'DONE', 'UI App', NOW(), NULL, 1),
(NOW(), 'JUnit testing examples', NULL, NOW(), '/projects/testing', 0, 'DONE', 'Testing Suite', NOW(), NULL, 1),
(NOW(), 'Database schema lab', NULL, NOW(), '/projects/db', 0, 'IN_PROGRESS', 'Database Lab', NOW(), NULL, 1),
(NOW(), 'DevOps CI pipeline', NULL, NOW(), '/projects/ci', 0, 'NOT_STARTED', 'CI Pipeline', NOW(), NULL, 1),
(NOW(), 'Java Collections deep dive', NULL, NOW(), '/projects/collections', 0, 'DONE', 'Collections Study', NOW(), NULL, 1),
(NOW(), 'Security filters project', NULL, NOW(), '/projects/filters', 0, 'IN_PROGRESS', 'Security Filters', NOW(), NULL, 1),
(NOW(), 'Hibernate experiments', NULL, NOW(), '/projects/hibernate', 0, 'DONE', 'Hibernate Lab', NOW(), NULL, 1),
(NOW(), 'JWT playground', NULL, NOW(), '/projects/jwt', 0, 'IN_PROGRESS', 'JWT Playground', NOW(), NULL, 1),
(NOW(), 'Spring Data research', NULL, NOW(), '/projects/data', 0, 'NOT_STARTED', 'Spring Data Lab', NOW(), NULL, 1),
(NOW(), 'Frontend templates', NULL, NOW(), '/projects/frontend', 0, 'DONE', 'Frontend Templates', NOW(), NULL, 1),

-- Vlad projects (user_id = 2)
(NOW(), 'Portfolio website', NULL, NOW(), '/projects/portfolio', 0, 'DONE', 'Portfolio Site', NOW(), NULL, 2),
(NOW(), 'E-commerce API', NULL, NOW(), '/projects/ecommerce', 0, 'IN_PROGRESS', 'E-commerce API', NOW(), NULL, 2),
(NOW(), 'Spring Security OAuth2', NULL, NOW(), '/projects/oauth', 0, 'NOT_STARTED', 'OAuth2 App', NOW(), NULL, 2),
(NOW(), 'Chat application', NULL, NOW(), '/projects/chat', 0, 'IN_PROGRESS', 'Chat App', NOW(), NULL, 2),
(NOW(), 'File manager', NULL, NOW(), '/projects/files', 0, 'DONE', 'File Manager', NOW(), NULL, 2),
(NOW(), 'Analytics dashboard', NULL, NOW(), '/projects/dashboard', 0, 'IN_PROGRESS', 'Analytics Dashboard', NOW(), NULL, 2),
(NOW(), 'Learning Spring Boot', NULL, NOW(), '/projects/learning-sb', 0, 'DONE', 'Spring Boot Study', NOW(), NULL, 2),
(NOW(), 'Docker compose lab', NULL, NOW(), '/projects/docker-compose', 0, 'NOT_STARTED', 'Docker Compose Lab', NOW(), NULL, 2),
(NOW(), 'Blog platform', NULL, NOW(), '/projects/blog', 0, 'DONE', 'Blog Platform', NOW(), NULL, 2),
(NOW(), 'Authentication system', NULL, NOW(), '/projects/auth', 0, 'IN_PROGRESS', 'Auth System', NOW(), NULL, 2),
(NOW(), 'Monitoring tool', NULL, NOW(), '/projects/monitor', 0, 'NOT_STARTED', 'Monitoring Tool', NOW(), NULL, 2),
(NOW(), 'REST microservice', NULL, NOW(), '/projects/rest-micro', 0, 'DONE', 'REST Microservice', NOW(), NULL, 2),
(NOW(), 'Task manager', NULL, NOW(), '/projects/tasks', 0, 'IN_PROGRESS', 'Task Manager', NOW(), NULL, 2),
(NOW(), 'Security demo', NULL, NOW(), '/projects/security-demo', 0, 'DONE', 'Security Demo', NOW(), NULL, 2),
(NOW(), 'Testing playground', NULL, NOW(), '/projects/testing-play', 0, 'NOT_STARTED', 'Testing Playground', NOW(), NULL, 2);

-- project and tags relations (testing and add by ui)



