package com.vbforge.projectstracker.repository;

import com.vbforge.projectstracker.entity.Project;
import com.vbforge.projectstracker.entity.ProjectStatus;
import com.vbforge.projectstracker.entity.Tag;
import com.vbforge.projectstracker.entity.User;
import com.vbforge.projectstracker.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ProjectRepository Tests")
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    private User user1;
    private User user2;
    private Project project1;
    private Project project2;
    private Project project3;
    private Tag tag1;
    private Tag tag2;

    @BeforeEach
    void setUp() {
        // Clean up
        projectRepository.deleteAll();
        tagRepository.deleteAll();
        userRepository.deleteAll();

        // Create users
        user1 = User.builder()
                .username("testuser1")
                .email("test1@example.com")
                .password("password")
                .role(Role.USER)
                .enabled(true)
                .build();
        user1 = userRepository.save(user1);

        user2 = User.builder()
                .username("testuser2")
                .email("test2@example.com")
                .password("password")
                .role(Role.USER)
                .enabled(true)
                .build();
        user2 = userRepository.save(user2);

        // Create tags for user1
        tag1 = Tag.builder()
                .name("Spring Boot")
                .color("#28a745")
                .description("Java framework")
                .owner(user1)
                .build();
        tag1 = tagRepository.save(tag1);

        tag2 = Tag.builder()
                .name("React")
                .color("#61dafb")
                .description("JS library")
                .owner(user1)
                .build();
        tag2 = tagRepository.save(tag2);

        // Create projects
        project1 = Project.builder()
                .title("Project 1")
                .description("Description 1")
                .status(ProjectStatus.IN_PROGRESS)
                .onGithub(true)
                .githubUrl("https://github.com/user/project1")
                .owner(user1)
                .build();
        project1.addTag(tag1);
        project1 = projectRepository.save(project1);

        project2 = Project.builder()
                .title("Project 2")
                .description("Description 2")
                .status(ProjectStatus.DONE)
                .onGithub(false)
                .owner(user1)
                .build();
        project2 = projectRepository.save(project2);

        project3 = Project.builder()
                .title("User2 Project")
                .description("Should not appear in user1 queries")
                .status(ProjectStatus.IN_PROGRESS)
                .owner(user2)
                .build();
        project3 = projectRepository.save(project3);
    }

    @Test
    @DisplayName("Should find all projects by owner")
    void shouldFindAllByOwner() {
        // When
        List<Project> projects = projectRepository.findAllByOwner(user1);

        // Then
        assertThat(projects).hasSize(2);
        assertThat(projects).extracting(Project::getTitle)
                .containsExactlyInAnyOrder("Project 1", "Project 2");
    }

    @Test
    @DisplayName("Should find project by ID and owner")
    void shouldFindByIdAndOwner() {
        // When
        Optional<Project> found = projectRepository.findByIdAndOwner(project1.getId(), user1);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Project 1");
    }

    @Test
    @DisplayName("Should not find project of different owner")
    void shouldNotFindProjectOfDifferentOwner() {
        // When
        Optional<Project> found = projectRepository.findByIdAndOwner(project3.getId(), user1);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find projects by status and owner")
    void shouldFindByStatusAndOwner() {
        // When
        List<Project> inProgress = projectRepository.findByStatusAndOwner(ProjectStatus.IN_PROGRESS, user1);
        List<Project> done = projectRepository.findByStatusAndOwner(ProjectStatus.DONE, user1);

        // Then
        assertThat(inProgress).hasSize(1);
        assertThat(inProgress.get(0).getTitle()).isEqualTo("Project 1");
        assertThat(done).hasSize(1);
        assertThat(done.get(0).getTitle()).isEqualTo("Project 2");
    }

    @Test
    @DisplayName("Should find projects by GitHub status and owner")
    void shouldFindByOnGithubAndOwner() {
        // When
        List<Project> githubProjects = projectRepository.findByOnGithubAndOwner(true, user1);
        List<Project> localProjects = projectRepository.findByOnGithubAndOwner(false, user1);

        // Then
        assertThat(githubProjects).hasSize(1);
        assertThat(githubProjects.get(0).getTitle()).isEqualTo("Project 1");
        assertThat(localProjects).hasSize(1);
        assertThat(localProjects.get(0).getTitle()).isEqualTo("Project 2");
    }

    @Test
    @DisplayName("Should find projects by title containing (case-insensitive)")
    void shouldFindByTitleContainingIgnoreCaseAndOwner() {
        // When
        List<Project> found = projectRepository.findByTitleContainingIgnoreCaseAndOwner("project", user1);

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Project::getTitle)
                .containsExactlyInAnyOrder("Project 1", "Project 2");
    }

    @Test
    @DisplayName("Should count projects by owner")
    void shouldCountByOwner() {
        // When
        long count = projectRepository.countByOwner(user1);

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should count projects by status and owner")
    void shouldCountByStatusAndOwner() {
        // When
        long inProgressCount = projectRepository.countByStatusAndOwner(ProjectStatus.IN_PROGRESS, user1);
        long doneCount = projectRepository.countByStatusAndOwner(ProjectStatus.DONE, user1);
        long notStartedCount = projectRepository.countByStatusAndOwner(ProjectStatus.NOT_STARTED, user1);

        // Then
        assertThat(inProgressCount).isEqualTo(1);
        assertThat(doneCount).isEqualTo(1);
        assertThat(notStartedCount).isZero();
    }

    @Test
    @DisplayName("Should count projects by GitHub status and owner")
    void shouldCountByOnGithubAndOwner() {
        // When
        long githubCount = projectRepository.countByOnGithubAndOwner(true, user1);
        long localCount = projectRepository.countByOnGithubAndOwner(false, user1);

        // Then
        assertThat(githubCount).isEqualTo(1);
        assertThat(localCount).isEqualTo(1);
    }

    @Test
    @DisplayName("Should find projects by tag name and owner")
    void shouldFindByTagNameAndOwner() {
        // When
        List<Project> projects = projectRepository.findByTagNameAndOwner("Spring Boot", user1);

        // Then
        assertThat(projects).hasSize(1);
        assertThat(projects.get(0).getTitle()).isEqualTo("Project 1");
    }

    @Test
    @DisplayName("Should find projects by multiple tag names and owner")
    void shouldFindByTagNamesAndOwner() {
        // Given
        project2.addTag(tag2);
        projectRepository.save(project2);

        // When
        List<Project> projects = projectRepository.findByTagNamesAndOwner(
                List.of("Spring Boot", "React"), user1);

        // Then
        assertThat(projects).hasSize(2);
    }

    @Test
    @DisplayName("Should find projects created between dates")
    void shouldFindByCreatedDateBetweenAndOwner() {
        // Given
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        // When
        List<Project> projects = projectRepository.findByCreatedDateBetweenAndOwner(start, end, user1);

        // Then
        assertThat(projects).hasSize(2);
    }

    @Test
    @DisplayName("Should search projects with multiple filters")
    void shouldSearchProjects() {
        // When - Search by title only
        List<Project> byTitle = projectRepository.searchProjects(user1, "Project 1", null, null, null);

        // Then
        assertThat(byTitle).hasSize(1);
        assertThat(byTitle.get(0).getTitle()).isEqualTo("Project 1");

        // When - Search by status only
        List<Project> byStatus = projectRepository.searchProjects(user1, null, ProjectStatus.DONE, null, null);

        // Then
        assertThat(byStatus).hasSize(1);
        assertThat(byStatus.get(0).getStatus()).isEqualTo(ProjectStatus.DONE);

        // When - Combined search
        List<Project> combined = projectRepository.searchProjects(
                user1, "Project", ProjectStatus.IN_PROGRESS, true, null);

        // Then
        assertThat(combined).hasSize(1);
        assertThat(combined.get(0).getTitle()).isEqualTo("Project 1");
    }

    @Test
    @DisplayName("Should order projects by last worked on descending")
    void shouldFindAllByOwnerOrderByLastWorkedOnDesc() {
        // Given - Update timestamps
        project1.setLastWorkedOn(LocalDateTime.now().minusDays(2));
        project2.setLastWorkedOn(LocalDateTime.now().minusDays(1));
        projectRepository.saveAll(List.of(project1, project2));

        // When
        List<Project> projects = projectRepository.findAllByOwnerOrderByLastWorkedOnDesc(user1);

        // Then
        assertThat(projects).hasSize(2);
        assertThat(projects.get(0).getTitle()).isEqualTo("Project 2"); // Most recent
        assertThat(projects.get(1).getTitle()).isEqualTo("Project 1");
    }

    @Test
    @DisplayName("Should order projects by created date descending")
    void shouldFindAllByOwnerOrderByCreatedDateDesc() {
        // When
        List<Project> projects = projectRepository.findAllByOwnerOrderByCreatedDateDesc(user1);

        // Then
        assertThat(projects).hasSize(2);
        // Most recently created should be first
    }

    @Test
    @DisplayName("Should enforce data isolation between users")
    void shouldEnforceDataIsolation() {
        // When
        List<Project> user1Projects = projectRepository.findAllByOwner(user1);
        List<Project> user2Projects = projectRepository.findAllByOwner(user2);

        // Then
        assertThat(user1Projects).hasSize(2);
        assertThat(user2Projects).hasSize(1);
        assertThat(user1Projects).extracting(Project::getTitle)
                .doesNotContain("User2 Project");
        assertThat(user2Projects).extracting(Project::getTitle)
                .containsExactly("User2 Project");
    }
}