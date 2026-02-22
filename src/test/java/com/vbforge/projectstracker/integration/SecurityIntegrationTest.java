package com.vbforge.projectstracker.integration;

import com.vbforge.projectstracker.entity.*;
import com.vbforge.projectstracker.repository.ProjectRepository;
import com.vbforge.projectstracker.repository.TagRepository;
import com.vbforge.projectstracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Security Integration Test
 * Verifies data isolation and security constraints at database level
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Security & Data Isolation Integration Test")
class SecurityIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TagRepository tagRepository;

    private User alice;
    private User bob;

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();
        tagRepository.deleteAll();
        userRepository.deleteAll();

        alice = createUser("alice", "alice@example.com");
        bob = createUser("bob", "bob@example.com");
    }

    @Test
    @DisplayName("Should enforce user isolation for projects")
    void shouldEnforceUserIsolationForProjects() {
        // Given: Alice creates projects
        Project aliceProject1 = createProject("Alice Project 1", alice);
        Project aliceProject2 = createProject("Alice Project 2", alice);

        // And: Bob creates projects
        Project bobProject1 = createProject("Bob Project 1", bob);

        // When: Query projects by owner
        List<Project> aliceProjects = projectRepository.findAllByOwner(alice);
        List<Project> bobProjects = projectRepository.findAllByOwner(bob);

        // Then: Each user sees only their projects
        assertThat(aliceProjects).hasSize(2);
        assertThat(aliceProjects).extracting(Project::getTitle)
                .containsExactlyInAnyOrder("Alice Project 1", "Alice Project 2");

        assertThat(bobProjects).hasSize(1);
        assertThat(bobProjects).extracting(Project::getTitle)
                .containsExactly("Bob Project 1");

        // Critical: Bob cannot access Alice's project by ID
        Optional<Project> aliceProjectAsBob = projectRepository
                .findByIdAndOwner(aliceProject1.getId(), bob);
        assertThat(aliceProjectAsBob).isEmpty();

        // Alice can access her own project
        Optional<Project> aliceProjectAsAlice = projectRepository
                .findByIdAndOwner(aliceProject1.getId(), alice);
        assertThat(aliceProjectAsAlice).isPresent();
    }

    @Test
    @DisplayName("Should enforce user isolation for tags")
    void shouldEnforceUserIsolationForTags() {
        // Given: Alice creates tags
        Tag aliceTag1 = createTag("Java", "#007396", alice);
        Tag aliceTag2 = createTag("Spring", "#6db33f", alice);

        // And: Bob creates tags
        Tag bobTag1 = createTag("Python", "#3776ab", bob);

        // When: Query tags by owner
        List<Tag> aliceTags = tagRepository.findAllByOwner(alice);
        List<Tag> bobTags = tagRepository.findAllByOwner(bob);

        // Then: Each user sees only their tags
        assertThat(aliceTags).hasSize(2);
        assertThat(aliceTags).extracting(Tag::getName)
                .containsExactlyInAnyOrder("Java", "Spring");

        assertThat(bobTags).hasSize(1);
        assertThat(bobTags).extracting(Tag::getName)
                .containsExactly("Python");

        // Critical: Bob cannot access Alice's tag by ID
        Optional<Tag> aliceTagAsBob = tagRepository
                .findByIdAndOwner(aliceTag1.getId(), bob);
        assertThat(aliceTagAsBob).isEmpty();
    }

    @Test
    @DisplayName("Should allow same tag name for different users")
    void shouldAllowSameTagNameForDifferentUsers() {
        // Given: Both users create "JavaScript" tag
        Tag aliceJS = createTag("JavaScript", "#f7df1e", alice);
        Tag bobJS = createTag("JavaScript", "#f7df1e", bob);

        // Then: Both tags exist with different IDs
        assertThat(aliceJS.getId()).isNotNull();
        assertThat(bobJS.getId()).isNotNull();
        assertThat(aliceJS.getId()).isNotEqualTo(bobJS.getId());

        // Each user can find their own tag
        Optional<Tag> aliceJSFound = tagRepository.findByNameAndOwner("JavaScript", alice);
        Optional<Tag> bobJSFound = tagRepository.findByNameAndOwner("JavaScript", bob);

        assertThat(aliceJSFound).isPresent();
        assertThat(bobJSFound).isPresent();
        assertThat(aliceJSFound.get().getOwner()).isEqualTo(alice);
        assertThat(bobJSFound.get().getOwner()).isEqualTo(bob);
    }

    @Test
    @DisplayName("Should enforce tag uniqueness per user")
    void shouldEnforceTagUniquenessPerUser() {
        // Given: Alice creates "Docker" tag
        createTag("Docker", "#2496ed", alice);

        // When: Check if Alice can create another "Docker" tag
        boolean exists = tagRepository.existsByNameAndOwner("Docker", alice);

        // Then: Tag exists for Alice
        assertThat(exists).isTrue();

        // But not for Bob
        boolean existsForBob = tagRepository.existsByNameAndOwner("Docker", bob);
        assertThat(existsForBob).isFalse();
    }

    @Test
    @DisplayName("Should isolate project-tag relationships")
    void shouldIsolateProjectTagRelationships() {
        // Given: Alice creates project with tags
        Tag aliceTag = createTag("React", "#61dafb", alice);
        Project aliceProject = createProject("Alice React App", alice);
        aliceProject.addTag(aliceTag);
        aliceProject = projectRepository.save(aliceProject);

        // And: Bob creates project with tags
        Tag bobTag = createTag("Vue", "#42b883", bob);
        Project bobProject = createProject("Bob Vue App", bob);
        bobProject.addTag(bobTag);
        bobProject = projectRepository.save(bobProject);

        // When: Query projects by tag
        List<Project> reactProjects = projectRepository.findByTagNameAndOwner("React", alice);
        List<Project> vueProjects = projectRepository.findByTagNameAndOwner("Vue", bob);

        // Then: Each query returns only owner's projects
        assertThat(reactProjects).hasSize(1);
        assertThat(reactProjects.get(0).getOwner()).isEqualTo(alice);

        assertThat(vueProjects).hasSize(1);
        assertThat(vueProjects.get(0).getOwner()).isEqualTo(bob);

        // Alice cannot find Bob's Vue projects
        List<Project> aliceVueProjects = projectRepository.findByTagNameAndOwner("Vue", alice);
        assertThat(aliceVueProjects).isEmpty();
    }

    @Test
    @DisplayName("Should count projects correctly per user")
    void shouldCountProjectsCorrectlyPerUser() {
        // Given: Alice creates 3 projects, Bob creates 2
        createProject("Alice P1", alice);
        createProject("Alice P2", alice);
        createProject("Alice P3", alice);
        createProject("Bob P1", bob);
        createProject("Bob P2", bob);

        // When: Count projects
        long aliceCount = projectRepository.countByOwner(alice);
        long bobCount = projectRepository.countByOwner(bob);

        // Then: Counts are correct
        assertThat(aliceCount).isEqualTo(3);
        assertThat(bobCount).isEqualTo(2);
    }

    @Test
    @DisplayName("Should filter by status per user")
    void shouldFilterByStatusPerUser() {
        // Given: Users create projects with different statuses
        createProjectWithStatus("Alice Done", alice, ProjectStatus.DONE);
        createProjectWithStatus("Alice In Progress", alice, ProjectStatus.IN_PROGRESS);
        createProjectWithStatus("Bob Done", bob, ProjectStatus.DONE);

        // When: Query DONE projects
        List<Project> aliceDone = projectRepository.findByStatusAndOwner(ProjectStatus.DONE, alice);
        List<Project> bobDone = projectRepository.findByStatusAndOwner(ProjectStatus.DONE, bob);

        // Then: Each user sees only their DONE projects
        assertThat(aliceDone).hasSize(1);
        assertThat(aliceDone.get(0).getTitle()).isEqualTo("Alice Done");

        assertThat(bobDone).hasSize(1);
        assertThat(bobDone.get(0).getTitle()).isEqualTo("Bob Done");
    }

    @Test
    @DisplayName("Should delete user cascade deletes their data")
    void shouldDeleteUserCascadeDeletesTheirData() {
        // Given: Alice creates data
        createTag("AliceTag", "#000000", alice);
        createProject("AliceProject", alice);

        Long aliceId = alice.getId();

        // Verify data exists
        assertThat(projectRepository.findAllByOwner(alice)).hasSize(1);
        assertThat(tagRepository.findAllByOwner(alice)).hasSize(1);

        // When: Delete Alice
        userRepository.delete(alice);

        // Then: Alice's projects and tags are also deleted (cascade)
        assertThat(userRepository.findById(aliceId)).isEmpty();
        
        // Bob's data is unaffected
        assertThat(userRepository.findById(bob.getId())).isPresent();
    }

    // ========== HELPER METHODS ==========

    private User createUser(String username, String email) {
        User user = User.builder()
                .username(username)
                .email(email)
                .password("encodedPassword")
                .role(Role.USER)
                .enabled(true)
                .build();
        return userRepository.save(user);
    }

    private Project createProject(String title, User owner) {
        return createProjectWithStatus(title, owner, ProjectStatus.IN_PROGRESS);
    }

    private Project createProjectWithStatus(String title, User owner, ProjectStatus status) {
        Project project = Project.builder()
                .title(title)
                .description("Test description")
                .status(status)
                .onGithub(false)
                .owner(owner)
                .build();
        return projectRepository.save(project);
    }

    private Tag createTag(String name, String color, User owner) {
        Tag tag = Tag.builder()
                .name(name)
                .color(color)
                .owner(owner)
                .build();
        return tagRepository.save(tag);
    }
}
