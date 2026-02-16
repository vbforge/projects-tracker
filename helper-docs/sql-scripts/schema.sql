CREATE TABLE projects (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          title VARCHAR(255) NOT NULL,
                          description TEXT,
                          status ENUM('NOT_STARTED', 'IN_PROGRESS', 'DONE') NOT NULL DEFAULT 'NOT_STARTED',
                          on_github BOOLEAN DEFAULT FALSE,
                          github_url VARCHAR(500),
                          local_path VARCHAR(500),
                          what_todo TEXT,
                          created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          last_worked_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE tags (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(100) NOT NULL UNIQUE,
                      color VARCHAR(7) DEFAULT '#e7f3ff',
                      description VARCHAR(255),
                      created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE project_tags (
                              project_id BIGINT NOT NULL,
                              tag_id BIGINT NOT NULL,
                              assigned_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              PRIMARY KEY (project_id, tag_id),
                              CONSTRAINT fk_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
                              CONSTRAINT fk_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- Indexes
CREATE INDEX idx_project_status ON projects(status);
CREATE INDEX idx_project_github ON projects(on_github);
CREATE INDEX idx_project_created_date ON projects(created_date);
CREATE INDEX idx_project_last_worked ON projects(last_worked_on);
CREATE INDEX idx_tag_name ON tags(name);