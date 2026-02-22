package com.vbforge.projectstracker.integration;

import com.vbforge.projectstracker.entity.*;
import com.vbforge.projectstracker.repository.ProjectRepository;
import com.vbforge.projectstracker.repository.TagRepository;
import com.vbforge.projectstracker.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Database Schema and Constraints Integration Test
 * Verifies JPA mappings, constraints, and database schema
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Database Schema Integration Test")
class DatabaseIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TagRepository tagRepository;

    @Test
    @DisplayName("Should create all required database tables")
    void shouldCreateAllRequiredDatabaseTables() {
        // Verify users table
        Long usersCount = (Long) entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE UPPER(TABLE_NAME) = 'USERS' AND TABLE_SCHEMA = 'PUBLIC'",
                Long.class).getSingleResult();
        assertThat(usersCount).isEqualTo(1L);

        // Verify projects table
        Long projectsCount = (Long) entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE UPPER(TABLE_NAME) = 'PROJECTS'",
                Long.class).getSingleResult();
        assertThat(projectsCount).isEqualTo(1L);

        // Verify tags table
        Long tagsCount = (Long) entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE UPPER(TABLE_NAME) = 'TAGS'",
                Long.class).getSingleResult();
        assertThat(tagsCount).isEqualTo(1L);

        // Verify project_tags junction table
        Long junctionCount = (Long) entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE UPPER(TABLE_NAME) = 'PROJECT_TAGS'",
                Long.class).getSingleResult();
        assertThat(junctionCount).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should enforce NOT NULL constraints")
    void shouldEnforceNotNullConstraints() {
        // User without username should fail
        User invalidUser = User.builder()
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .build();

        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
            userRepository.save(invalidUser);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Should set default values on persist")
    void shouldSetDefaultValuesOnPersist() {
        // Given
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .role(Role.USER)
                .enabled(true)
                .build();
        user = userRepository.save(user);

        Project project = Project.builder()
                .title("Test Project")
                .owner(user)
                .build();

        // When
        project = projectRepository.save(project);
        entityManager.flush();

        // Then: Default values are set
        assertThat(project.getStatus()).isEqualTo(ProjectStatus.NOT_STARTED);
        assertThat(project.getOnGithub()).isFalse();
        assertThat(project.getCreatedDate()).isNotNull();
        assertThat(project.getLastWorkedOn()).isNotNull();
        assertThat(project.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should handle many-to-many relationship correctly")
    void shouldHandleManyToManyRelationshipCorrectly() {
        // Given
        User user = createUser("testuser");
        Tag tag1 = createTag("Tag1", user);
        Tag tag2 = createTag("Tag2", user);
        
        Project project1 = createProject("Project1", user);
        Project project2 = createProject("Project2", user);

        // When: Add tags to projects
        project1.addTag(tag1);
        project1.addTag(tag2);
        project2.addTag(tag1);

        projectRepository.save(project1);
        projectRepository.save(project2);
        entityManager.flush();
        entityManager.clear();

        // Then: Verify relationships
        Project foundProject1 = projectRepository.findById(project1.getId()).orElseThrow();
        assertThat(foundProject1.getTags()).hasSize(2);

        Project foundProject2 = projectRepository.findById(project2.getId()).orElseThrow();
        assertThat(foundProject2.getTags()).hasSize(1);

        Tag foundTag1 = tagRepository.findById(tag1.getId()).orElseThrow();
        assertThat(foundTag1.getProjects()).hasSize(2);
    }

    @Test
    @DisplayName("Should update updatedAt timestamp on entity change")
    void shouldUpdateUpdatedAtTimestampOnEntityChange() throws InterruptedException {
        // Given
        User user = createUser("testuser");
        Project project = createProject("Test Project", user);
        
        java.time.LocalDateTime originalUpdatedAt = project.getUpdatedAt();

        // Wait a bit to ensure time difference
        Thread.sleep(10);

        // When: Update project
        project.setTitle("Updated Title");
        project = projectRepository.save(project);
        entityManager.flush();

        // Then: updatedAt should be newer
        assertThat(project.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    @DisplayName("Should query with complex filters")
    void shouldQueryWithComplexFilters() {
        // Given
        User user = createUser("testuser");
        Tag tag = createTag("ImportantTag", user);

        Project p1 = createProjectWithDetails("Project A", ProjectStatus.IN_PROGRESS, true, user);
        p1.addTag(tag);
        projectRepository.save(p1);

        createProjectWithDetails("Project B", ProjectStatus.DONE, false, user);
        createProjectWithDetails("Project C", ProjectStatus.IN_PROGRESS, false, user);

        // When: Search with multiple filters
        List<Project> results = projectRepository.searchProjects(
                user, "Project", ProjectStatus.IN_PROGRESS, true, null);

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Project A");
    }

    // Helper methods
    private User createUser(String username) {
        User user = User.builder()
                .username(username)
                .email(username + "@example.com")
                .password("password")
                .role(Role.USER)
                .enabled(true)
                .build();
        return userRepository.save(user);
    }

    private Tag createTag(String name, User owner) {
        Tag tag = Tag.builder()
                .name(name)
                .color("#000000")
                .owner(owner)
                .build();
        return tagRepository.save(tag);
    }

    private Project createProject(String title, User owner) {
        return createProjectWithDetails(title, ProjectStatus.IN_PROGRESS, false, owner);
    }

    private Project createProjectWithDetails(String title, ProjectStatus status, 
                                           boolean onGithub, User owner) {
        Project project = Project.builder()
                .title(title)
                .description("Test description")
                .status(status)
                .onGithub(onGithub)
                .owner(owner)
                .build();
        return projectRepository.save(project);
    }
}
